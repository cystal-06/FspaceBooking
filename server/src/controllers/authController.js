const db = require('../config/db');

// Map DB status to App Status string
function mapUserStatus(status) {
    if (status === 'ACTIVE') return 'ĐANG HOẠT ĐỘNG';
    if (status === 'LOCKED') return 'ĐÃ KHÓA';
    return 'CẢNH BÁO';
}

exports.login = async (req, res) => {
    try {
        const { username, securityHash } = req.body;
        if (!username) {
            return res.status(400).json({ message: 'Tên đăng nhập không được để trống.' });
        }

        const trimmedUser = username.trim();
        // Query user with role and department
        const queryText = `
            SELECT u.*, r.role_code, d.department_code, d.department_name
            FROM users u
            JOIN roles r ON u.role_id = r.role_id
            JOIN departments d ON u.department_id = d.department_id
            WHERE LOWER(u.username) = LOWER($1) 
               OR LOWER(u.email) = LOWER($1)
               OR LOWER(u.employee_id) = LOWER($1)
            LIMIT 1;
        `;
        const result = await db.query(queryText, [trimmedUser]);

        let userRow;
        if (result.rows.length === 0) {
            // If user doesn't exist yet, auto-provision fallback user into DB
            const isAdm = trimmedUser.toLowerCase().includes('admin');
            const newId = `usr_${trimmedUser.toLowerCase().replace(/[^a-z0-9]/g, '')}`;
            const empId = isAdm ? 'ADM999' : `EMP${Math.floor(100 + Math.random() * 899)}`;
            const roleId = isAdm ? 1 : 2;
            const insertQuery = `
                INSERT INTO users (user_id, employee_id, username, password_hash, email, full_name, role_id, department_id, violation_count, user_status)
                VALUES ($1, $2, $3, $4, $5, $6, $7, 2, 0, 'ACTIVE')
                RETURNING *;
            `;
            const inserted = await db.query(insertQuery, [
                newId,
                empId,
                trimmedUser,
                securityHash || '123456',
                `${trimmedUser}@fpt.com`,
                isAdm ? 'Quản Trị Viên' : `Nhân Viên FPT (${trimmedUser})`,
                roleId
            ]);
            userRow = inserted.rows[0];
            userRow.role_code = isAdm ? 'ADMIN' : 'EMPLOYEE';
            userRow.department_code = 'FSU1.BU2';
        } else {
            userRow = result.rows[0];
        }

        // Write Audit Log
        const logId = `log_${Date.now()}`;
        const logMsg = `Người dùng '${userRow.full_name}' (${userRow.employee_id}) đăng nhập thành công vào hệ thống.`;
        await db.query(`
            INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json)
            VALUES ($1, $2, 'AUTH', 'USER_LOGIN', 'users', $3, $4);
        `, [logId, userRow.user_id, userRow.user_id, JSON.stringify({ ip: req.ip, user_agent: req.headers['user-agent'] })]);

        // Return User payload formatted for Android Model
        const userPayload = {
            id: userRow.user_id,
            username: userRow.username,
            email: userRow.email,
            name: userRow.full_name,
            role: userRow.role_code === 'ADMIN' ? 'ADMIN' : 'EMPLOYEE',
            employeeId: userRow.employee_id,
            token: `fspace_jwt_${userRow.user_id}_${Date.now()}`,
            violations: userRow.violation_count || 0,
            userStatus: mapUserStatus(userRow.user_status),
            fsu: userRow.department_code || 'FSU1.BU2'
        };

        return res.status(200).json(userPayload);
    } catch (err) {
        console.error('Login error:', err);
        return res.status(500).json({ message: 'Lỗi máy chủ khi đăng nhập: ' + err.message });
    }
};

exports.logout = async (req, res) => {
    return res.status(200).json({ success: true, message: 'Đăng xuất thành công' });
};
