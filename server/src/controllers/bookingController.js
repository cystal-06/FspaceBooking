const db = require('../config/db');

// Helper to format booking row for Android Booking domain model
function formatBooking(row) {
    return {
        id: row.booking_id,
        userId: row.user_id,
        username: row.username || row.full_name || 'Nhân viên FPT',
        resource: {
            id: row.resource_id,
            name: row.resource_name,
            type: row.resource_type === 'DESK' ? 'DESK' : 'ROOM',
            capacity: parseInt(row.capacity, 10),
            building: row.building_name || 'F-Town 3',
            floor: row.floor_name || 'Tầng 1',
            status: 'AVAILABLE',
            description: row.description || '',
            amenities: ['Wi-Fi 6', 'Điều hòa', 'Bảng viết'],
            isFavorite: false,
            imageUrl: row.image_url || 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80'
        },
        timeSlot: {
            id: parseInt(row.slot_id, 10),
            startTime: row.start_time ? row.start_time.substring(0, 5) : '08:00',
            endTime: row.end_time ? row.end_time.substring(0, 5) : '10:00',
            label: row.slot_label || `Khung giờ ${row.slot_id}`
        },
        date: row.booking_date instanceof Date ? row.booking_date.toISOString().split('T')[0] : String(row.booking_date),
        status: row.booking_status, // 'CONFIRMED', 'CHECKED_IN', 'CANCELLED', 'NO_SHOW'
        purpose: row.purpose || 'Họp công việc nội bộ',
        createdAt: row.created_at ? new Date(row.created_at).toISOString() : new Date().toISOString()
    };
}

exports.createBooking = async (req, res) => {
    try {
        const { resourceId, date, timeSlotId, purpose, userId } = req.body;
        
        // Default to employee if userId not specified in request header/body
        const actualUserId = userId || req.headers['x-user-id'] || 'usr_namtv';
        const actualPurpose = purpose || 'Họp công việc nội bộ';

        if (!resourceId || !date || !timeSlotId) {
            return res.status(400).json({ message: 'Thiếu thông tin phòng, ngày hoặc khung giờ đặt.' });
        }

        // Execute PostgreSQL Safe Stored Procedure with Row-level Locking (sp_create_booking_safe)
        const spResult = await db.query(`
            SELECT success, booking_id_out, message_out 
            FROM sp_create_booking_safe($1, $2, $3, $4, $5, 1);
        `, [actualUserId, resourceId, parseInt(timeSlotId, 10), date, actualPurpose]);

        const { success, booking_id_out, message_out } = spResult.rows[0];

        if (!success) {
            return res.status(400).json({ message: message_out });
        }

        // Fetch newly created booking with full relation details
        const fetchQuery = `
            SELECT b.*, r.resource_name, r.resource_type, r.capacity, r.description, r.image_url,
                   bld.building_name, flr.floor_name,
                   ts.start_time, ts.end_time, ts.slot_label,
                   u.username, u.full_name
            FROM bookings b
            JOIN resources r ON b.resource_id = r.resource_id
            JOIN buildings bld ON r.building_id = bld.building_id
            JOIN floors flr ON r.floor_id = flr.floor_id
            JOIN time_slots ts ON b.slot_id = ts.slot_id
            JOIN users u ON b.user_id = u.user_id
            WHERE b.booking_id = $1;
        `;
        const bookingDetail = await db.query(fetchQuery, [booking_id_out]);

        return res.status(201).json(formatBooking(bookingDetail.rows[0]));
    } catch (err) {
        console.error('Error in createBooking:', err);
        return res.status(500).json({ message: 'Lỗi thực hiện đặt phòng: ' + err.message });
    }
};

exports.getAllBookings = async (req, res) => {
    try {
        const { userId, date } = req.query;
        let queryText = `
            SELECT b.*, r.resource_name, r.resource_type, r.capacity, r.description, r.image_url,
                   bld.building_name, flr.floor_name,
                   ts.start_time, ts.end_time, ts.slot_label,
                   u.username, u.full_name
            FROM bookings b
            JOIN resources r ON b.resource_id = r.resource_id
            JOIN buildings bld ON r.building_id = bld.building_id
            JOIN floors flr ON r.floor_id = flr.floor_id
            JOIN time_slots ts ON b.slot_id = ts.slot_id
            JOIN users u ON b.user_id = u.user_id
            WHERE 1=1
        `;
        const params = [];

        if (userId) {
            params.push(userId);
            queryText += ` AND b.user_id = $${params.length}`;
        }
        if (date) {
            params.push(date);
            queryText += ` AND b.booking_date = $${params.length}`;
        }

        queryText += ` ORDER BY b.booking_date DESC, ts.slot_id ASC;`;

        const result = await db.query(queryText, params);
        return res.status(200).json(result.rows.map(formatBooking));
    } catch (err) {
        console.error('Error in getAllBookings:', err);
        return res.status(500).json({ message: 'Lỗi tải lịch đặt chỗ: ' + err.message });
    }
};

exports.cancelBooking = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await db.query(`
            UPDATE bookings
            SET booking_status = 'CANCELLED', cancelled_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP
            WHERE booking_id = $1
            RETURNING *;
        `, [id]);

        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Không tìm thấy đơn đặt để hủy.' });
        }

        // Write Audit Log
        await db.query(`
            INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json)
            VALUES ($1, $2, 'BOOKING', 'CANCEL_BOOKING', 'bookings', $3, $4);
        `, [`log_${Date.now()}`, result.rows[0].user_id, id, JSON.stringify({ reason: 'Người dùng chủ động hủy' })]);

        return res.status(200).json({ success: true, message: 'Đã hủy lịch đặt phòng thành công.' });
    } catch (err) {
        console.error('Error in cancelBooking:', err);
        return res.status(500).json({ message: 'Lỗi hủy lịch đặt: ' + err.message });
    }
};

exports.checkInBooking = async (req, res) => {
    try {
        const { id } = req.params;
        const { qrHash } = req.body || {};

        // Execute stored procedure
        const spResult = await db.query(`
            SELECT success, message_out FROM sp_process_checkin($1, $2, $3);
        `, [id, qrHash || '', req.ip || '127.0.0.1']);

        const { success, message_out } = spResult.rows[0];
        if (!success) {
            return res.status(400).json({ message: message_out });
        }

        // Return updated booking
        const fetchQuery = `
            SELECT b.*, r.resource_name, r.resource_type, r.capacity, r.description, r.image_url,
                   bld.building_name, flr.floor_name,
                   ts.start_time, ts.end_time, ts.slot_label,
                   u.username, u.full_name
            FROM bookings b
            JOIN resources r ON b.resource_id = r.resource_id
            JOIN buildings bld ON r.building_id = bld.building_id
            JOIN floors flr ON r.floor_id = flr.floor_id
            JOIN time_slots ts ON b.slot_id = ts.slot_id
            JOIN users u ON b.user_id = u.user_id
            WHERE b.booking_id = $1;
        `;
        const updated = await db.query(fetchQuery, [id]);
        return res.status(200).json(formatBooking(updated.rows[0]));
    } catch (err) {
        console.error('Error in checkInBooking:', err);
        return res.status(500).json({ message: 'Lỗi điểm danh: ' + err.message });
    }
};

exports.getUtilizationStats = async (req, res) => {
    try {
        const result = await db.query(`SELECT * FROM v_resource_utilization;`);
        const statsMap = {};
        result.rows.forEach(r => {
            const count = parseInt(r.total_successful_bookings, 10) || 0;
            const rate = Math.min(95, 30 + count * 20);
            statsMap[r.resource_name] = rate;
        });
        return res.status(200).json(statsMap);
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi tải thống kê: ' + err.message });
    }
};
