const db = require('../config/db');

// Helper to format DB row to WorkspaceResource
function formatResourceRow(r, amenitiesList = []) {
    return {
        id: r.resource_id,
        name: r.resource_name,
        type: r.resource_type === 'DESK' ? 'DESK' : 'ROOM',
        capacity: parseInt(r.capacity, 10),
        building: r.building_name || 'F-Town 3',
        floor: r.floor_name || 'Tầng 1',
        status: r.status || 'AVAILABLE',
        description: r.description || '',
        amenities: amenitiesList.length > 0 ? amenitiesList : ['Wi-Fi 6', 'Điều hòa', 'Bảng viết'],
        isFavorite: false,
        imageUrl: r.image_url || 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80'
    };
}

exports.getResources = async (req, res) => {
    try {
        const { type, building, floor, capacity, search } = req.query;

        let queryText = `
            SELECT r.*, bld.building_name, flr.floor_name,
                   COALESCE(
                       (SELECT json_agg(a.amenity_name)
                        FROM resource_amenities ra
                        JOIN amenities a ON ra.amenity_id = a.amenity_id
                        WHERE ra.resource_id = r.resource_id), 
                       '[]'::json
                   ) AS amenities_json
            FROM resources r
            JOIN buildings bld ON r.building_id = bld.building_id
            JOIN floors flr ON r.floor_id = flr.floor_id
            WHERE r.is_active = TRUE
        `;
        const params = [];

        if (type) {
            params.push(type.toUpperCase());
            queryText += ` AND r.resource_type = $${params.length}`;
        }
        if (building) {
            params.push(`%${building}%`);
            queryText += ` AND bld.building_name ILIKE $${params.length}`;
        }
        if (floor) {
            params.push(`%${floor}%`);
            queryText += ` AND flr.floor_name ILIKE $${params.length}`;
        }
        if (capacity) {
            params.push(parseInt(capacity, 10));
            queryText += ` AND r.capacity >= $${params.length}`;
        }
        if (search) {
            params.push(`%${search}%`);
            queryText += ` AND (r.resource_name ILIKE $${params.length} OR r.description ILIKE $${params.length})`;
        }

        queryText += ` ORDER BY r.resource_type ASC, r.capacity DESC, r.resource_name ASC;`;

        const result = await db.query(queryText, params);
        const list = result.rows.map(r => formatResourceRow(r, r.amenities_json || []));

        return res.status(200).json(list);
    } catch (err) {
        console.error('Error in getResources:', err);
        return res.status(500).json({ message: 'Lỗi tải danh sách tài nguyên: ' + err.message });
    }
};

exports.getResourceById = async (req, res) => {
    try {
        const { id } = req.params;
        const queryText = `
            SELECT r.*, bld.building_name, flr.floor_name,
                   COALESCE(
                       (SELECT json_agg(a.amenity_name)
                        FROM resource_amenities ra
                        JOIN amenities a ON ra.amenity_id = a.amenity_id
                        WHERE ra.resource_id = r.resource_id), 
                       '[]'::json
                   ) AS amenities_json
            FROM resources r
            JOIN buildings bld ON r.building_id = bld.building_id
            JOIN floors flr ON r.floor_id = flr.floor_id
            WHERE r.resource_id = $1
            LIMIT 1;
        `;
        const result = await db.query(queryText, [id]);
        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Không tìm thấy thông tin phòng/bàn.' });
        }
        const row = result.rows[0];
        return res.status(200).json(formatResourceRow(row, row.amenities_json || []));
    } catch (err) {
        console.error('Error in getResourceById:', err);
        return res.status(500).json({ message: 'Lỗi máy chủ: ' + err.message });
    }
};

exports.getTimeSlots = async (req, res) => {
    try {
        const result = await db.query(`
            SELECT slot_id AS id, 
                   to_char(start_time, 'HH24:MI') AS "startTime", 
                   to_char(end_time, 'HH24:MI') AS "endTime", 
                   slot_label AS label
            FROM time_slots
            WHERE is_active = TRUE
            ORDER BY slot_id ASC;
        `);
        return res.status(200).json(result.rows);
    } catch (err) {
        console.error('Error in getTimeSlots:', err);
        return res.status(500).json({ message: 'Lỗi tải danh sách khung giờ: ' + err.message });
    }
};

exports.addResource = async (req, res) => {
    try {
        const r = req.body;
        const newId = r.id || `res_${Date.now()}`;
        const queryText = `
            INSERT INTO resources (resource_id, resource_code, resource_name, resource_type, building_id, floor_id, capacity, status, description)
            VALUES ($1, $2, $3, $4, 'BLD_FTOWN3', 'FLR_FT3_F1', $5, 'AVAILABLE', $6)
            RETURNING *;
        `;
        const result = await db.query(queryText, [
            newId,
            `CODE-${newId.toUpperCase()}`,
            r.name,
            r.type || 'ROOM',
            r.capacity || 1,
            r.description || ''
        ]);
        return res.status(201).json(formatResourceRow(result.rows[0], r.amenities || []));
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi thêm tài nguyên: ' + err.message });
    }
};

exports.updateResource = async (req, res) => {
    try {
        const { id } = req.params;
        const r = req.body;
        const queryText = `
            UPDATE resources
            SET resource_name = COALESCE($1, resource_name),
                capacity = COALESCE($2, capacity),
                description = COALESCE($3, description),
                updated_at = CURRENT_TIMESTAMP
            WHERE resource_id = $4
            RETURNING *;
        `;
        const result = await db.query(queryText, [r.name, r.capacity, r.description, id]);
        if (result.rows.length === 0) return res.status(404).json({ message: 'Không tìm thấy tài nguyên để cập nhật' });
        return res.status(200).json(formatResourceRow(result.rows[0], r.amenities || []));
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi cập nhật: ' + err.message });
    }
};

exports.deleteResource = async (req, res) => {
    try {
        const { id } = req.params;
        await db.query(`UPDATE resources SET is_active = FALSE WHERE resource_id = $1`, [id]);
        return res.status(200).json({ success: true, message: 'Đã xóa tài nguyên thành công' });
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi xóa tài nguyên: ' + err.message });
    }
};
