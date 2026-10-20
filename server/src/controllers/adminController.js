const db = require('../config/db');

function mapUserStatus(status) {
    if (status === 'ACTIVE') return 'ĐANG HOẠT ĐỘNG';
    if (status === 'LOCKED') return 'ĐÃ KHÓA';
    return 'CẢNH BÁO';
}

exports.getViolators = async (req, res) => {
    try {
        const queryText = `
            SELECT u.*, r.role_code, d.department_code, d.department_name
            FROM users u
            JOIN roles r ON u.role_id = r.role_id
            JOIN departments d ON u.department_id = d.department_id
            ORDER BY u.violation_count DESC, u.user_status DESC;
        `;
        const result = await db.query(queryText);
        const users = result.rows.map(userRow => ({
            id: userRow.user_id,
            username: userRow.username,
            email: userRow.email,
            name: userRow.full_name,
            role: userRow.role_code === 'ADMIN' ? 'ADMIN' : 'EMPLOYEE',
            employeeId: userRow.employee_id,
            token: null,
            violations: userRow.violation_count || 0,
            userStatus: mapUserStatus(userRow.user_status),
            fsu: userRow.department_code || 'FSU1.BU2'
        }));
        return res.status(200).json(users);
    } catch (err) {
        console.error('Error in getViolators:', err);
        return res.status(500).json({ message: 'Lỗi tải danh sách vi phạm: ' + err.message });
    }
};

exports.unlockUser = async (req, res) => {
    try {
        const { id } = req.params;
        await db.query(`
            UPDATE users
            SET user_status = 'ACTIVE', violation_count = 0, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = $1;
        `, [id]);

        // Audit log
        await db.query(`
            INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json)
            VALUES ($1, 'usr_admin', 'ADMIN', 'UNLOCK_USER', 'users', $2, $3);
        `, [`log_${Date.now()}`, id, JSON.stringify({ action: 'Mở khóa tài khoản & reset vi phạm' })]);

        return res.status(200).json({ success: true, message: 'Đã mở khóa tài khoản thành công.' });
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi mở khóa: ' + err.message });
    }
};

exports.lockUser = async (req, res) => {
    try {
        const { id } = req.params;
        await db.query(`
            UPDATE users
            SET user_status = 'LOCKED', updated_at = CURRENT_TIMESTAMP
            WHERE user_id = $1;
        `, [id]);

        // Audit log
        await db.query(`
            INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json)
            VALUES ($1, 'usr_admin', 'ADMIN', 'LOCK_USER', 'users', $2, $3);
        `, [`log_${Date.now()}`, id, JSON.stringify({ action: 'Khóa tài khoản theo yêu cầu Admin' })]);

        return res.status(200).json({ success: true, message: 'Đã khóa tài khoản thành công.' });
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi khóa tài khoản: ' + err.message });
    }
};

exports.getAuditLogs = async (req, res) => {
    try {
        const queryText = `
            SELECT log_id AS id, 
                   to_char(created_at, 'HH24:MI:SS') AS timestamp,
                   category,
                   action || ': ' || COALESCE(details_json->>'message', details_json->>'action', target_table) AS message
            FROM audit_logs
            ORDER BY created_at DESC
            LIMIT 50;
        `;
        const result = await db.query(queryText);
        return res.status(200).json(result.rows);
    } catch (err) {
        return res.status(500).json({ message: 'Lỗi tải nhật ký: ' + err.message });
    }
};
