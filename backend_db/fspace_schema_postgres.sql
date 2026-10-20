-- =============================================================================
-- F-SPACE WORKSPACE & MEETING ROOM BOOKING MANAGEMENT SYSTEM
-- ENTERPRISE RELATIONAL DATABASE SCHEMA (PostgreSQL 14+)
-- Project: Đồ Án Tốt Nghiệp / Graduation Thesis
-- Enterprise: FPT Software (F-Town 1, 2, 3 Campuses & FPT Tower)
-- =============================================================================

-- Enable Cryptographic Extension for Token Hashing
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------------------------------
-- 0. CLEANUP (For fresh install/re-run)
-- -----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_user_compliance_summary CASCADE;
DROP VIEW IF EXISTS v_resource_utilization CASCADE;
DROP VIEW IF EXISTS v_active_bookings CASCADE;

DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS violations CASCADE;
DROP TABLE IF EXISTS check_ins CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS time_slots CASCADE;
DROP TABLE IF EXISTS resource_amenities CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS amenities CASCADE;
DROP TABLE IF EXISTS floors CASCADE;
DROP TABLE IF EXISTS buildings CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- -----------------------------------------------------------------------------
-- 1. ROLES & PERMISSIONS
-- -----------------------------------------------------------------------------
CREATE TABLE roles (
    role_id         SERIAL PRIMARY KEY,
    role_code       VARCHAR(30) UNIQUE NOT NULL, -- 'ADMIN', 'EMPLOYEE', 'MANAGER'
    role_name       VARCHAR(100) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 2. DEPARTMENTS / FSU (FPT Strategic Units)
-- -----------------------------------------------------------------------------
CREATE TABLE departments (
    department_id   SERIAL PRIMARY KEY,
    department_code VARCHAR(50) UNIQUE NOT NULL, -- e.g. 'FHN.DX', 'FSU1.BU2', 'FPT_IS'
    department_name VARCHAR(150) NOT NULL,
    campus          VARCHAR(100) NOT NULL DEFAULT 'F-Town HCMC',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 3. USERS & IDENTITY
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    user_id         VARCHAR(50) PRIMARY KEY,     -- 'usr_admin', 'usr_namtv', etc.
    employee_id     VARCHAR(30) UNIQUE NOT NULL, -- FPT Staff Code: 'ADM001', 'EMP302'
    username        VARCHAR(50) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    email           VARCHAR(120) UNIQUE NOT NULL,
    phone           VARCHAR(20),
    full_name       VARCHAR(120) NOT NULL,
    role_id         INT NOT NULL REFERENCES roles(role_id) ON DELETE RESTRICT,
    department_id   INT NOT NULL REFERENCES departments(department_id) ON DELETE RESTRICT,
    violation_count INT NOT NULL DEFAULT 0 CHECK (violation_count >= 0),
    user_status     VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' 
                    CHECK (user_status IN ('ACTIVE', 'WARNING', 'LOCKED')),
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_users_status_violations ON users(user_status, violation_count);

-- -----------------------------------------------------------------------------
-- 4. BUILDINGS & CAMPUSES
-- -----------------------------------------------------------------------------
CREATE TABLE buildings (
    building_id     VARCHAR(50) PRIMARY KEY, -- 'BLD_FTOWN1', 'BLD_FTOWN2', 'BLD_FTOWN3'
    building_code   VARCHAR(30) UNIQUE NOT NULL,
    building_name   VARCHAR(100) NOT NULL,
    campus          VARCHAR(100) NOT NULL,
    address         TEXT NOT NULL,
    total_floors    INT NOT NULL DEFAULT 1 CHECK (total_floors > 0),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 5. FLOORS
-- -----------------------------------------------------------------------------
CREATE TABLE floors (
    floor_id        VARCHAR(50) PRIMARY KEY, -- 'FLR_FT3_F1', 'FLR_FT1_F2'
    building_id     VARCHAR(50) NOT NULL REFERENCES buildings(building_id) ON DELETE CASCADE,
    floor_number    INT NOT NULL,
    floor_name      VARCHAR(50) NOT NULL,
    floor_plan_asset TEXT, -- Blueprint image / SVG vector asset URI
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (building_id, floor_number)
);

-- -----------------------------------------------------------------------------
-- 6. AMENITIES (Tiện ích văn phòng)
-- -----------------------------------------------------------------------------
CREATE TABLE amenities (
    amenity_id      SERIAL PRIMARY KEY,
    amenity_code    VARCHAR(50) UNIQUE NOT NULL,
    amenity_name    VARCHAR(100) NOT NULL,
    icon_name       VARCHAR(50) NOT NULL,
    description     TEXT
);

-- -----------------------------------------------------------------------------
-- 7. WORKSPACE RESOURCES (Phòng họp & Bàn làm việc)
-- -----------------------------------------------------------------------------
CREATE TABLE resources (
    resource_id     VARCHAR(50) PRIMARY KEY,     -- 'res_1', 'res_2', etc.
    resource_code   VARCHAR(50) UNIQUE NOT NULL, -- 'ROOM-GOSLING-FT3'
    resource_name   VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(30) NOT NULL 
                    CHECK (resource_type IN ('ROOM', 'DESK', 'LAB', 'BOOTH')),
    building_id     VARCHAR(50) NOT NULL REFERENCES buildings(building_id) ON DELETE RESTRICT,
    floor_id        VARCHAR(50) NOT NULL REFERENCES floors(floor_id) ON DELETE RESTRICT,
    capacity        INT NOT NULL DEFAULT 1 CHECK (capacity > 0),
    status          VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE'
                    CHECK (status IN ('AVAILABLE', 'BOOKED', 'MAINTENANCE', 'DISABLED')),
    description     TEXT,
    image_url       TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resources_search ON resources(resource_type, status, building_id);
CREATE INDEX idx_resources_floor ON resources(floor_id);

-- -----------------------------------------------------------------------------
-- 8. RESOURCE_AMENITIES (Quan hệ N-N giữa Tài nguyên & Tiện ích)
-- -----------------------------------------------------------------------------
CREATE TABLE resource_amenities (
    resource_id     VARCHAR(50) NOT NULL REFERENCES resources(resource_id) ON DELETE CASCADE,
    amenity_id      INT NOT NULL REFERENCES amenities(amenity_id) ON DELETE CASCADE,
    quantity        INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    notes           VARCHAR(100),
    PRIMARY KEY (resource_id, amenity_id)
);

-- -----------------------------------------------------------------------------
-- 9. TIME SLOTS (Khung giờ đặt tiêu chuẩn)
-- -----------------------------------------------------------------------------
CREATE TABLE time_slots (
    slot_id         INT PRIMARY KEY,
    slot_code       VARCHAR(20) UNIQUE NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    slot_label      VARCHAR(50) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_slot_time CHECK (start_time < end_time)
);

-- -----------------------------------------------------------------------------
-- 10. BOOKINGS (Đơn đặt chỗ làm việc / phòng họp)
-- -----------------------------------------------------------------------------
CREATE TABLE bookings (
    booking_id          VARCHAR(50) PRIMARY KEY,     -- 'BK-20261020-001'
    booking_code        VARCHAR(50) UNIQUE NOT NULL,
    user_id             VARCHAR(50) NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    resource_id         VARCHAR(50) NOT NULL REFERENCES resources(resource_id) ON DELETE RESTRICT,
    slot_id             INT NOT NULL REFERENCES time_slots(slot_id) ON DELETE RESTRICT,
    booking_date        DATE NOT NULL,
    purpose             VARCHAR(255) NOT NULL DEFAULT 'Họp công việc nội bộ',
    attendee_count      INT NOT NULL DEFAULT 1 CHECK (attendee_count > 0),
    qr_code_hash        VARCHAR(255) NOT NULL,
    booking_status      VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED'
                        CHECK (booking_status IN ('CONFIRMED', 'CHECKED_IN', 'CANCELLED', 'NO_SHOW', 'COMPLETED')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at        TIMESTAMPTZ,
    cancellation_reason TEXT
);

-- ANTI-DOUBLE BOOKING CONSTRAINT (Partial Unique Index)
-- Ngăn ngừa triệt để tình trạng 2 người đặt trùng 1 tài nguyên tại cùng 1 ngày & khung giờ.
-- Nếu đơn đặt đã bị HỦY (CANCELLED), khung giờ lập tức được giải phóng cho người khác đặt.
CREATE UNIQUE INDEX uq_anti_double_booking 
ON bookings (resource_id, booking_date, slot_id) 
WHERE booking_status NOT IN ('CANCELLED');

CREATE INDEX idx_bookings_user_date ON bookings(user_id, booking_date);
CREATE INDEX idx_bookings_status ON bookings(booking_status, booking_date);

-- -----------------------------------------------------------------------------
-- 11. CHECK_INS (Điểm danh & Quét mã QR)
-- -----------------------------------------------------------------------------
CREATE TABLE check_ins (
    checkin_id      VARCHAR(50) PRIMARY KEY,
    booking_id      VARCHAR(50) UNIQUE NOT NULL REFERENCES bookings(booking_id) ON DELETE CASCADE,
    checked_in_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checkin_method  VARCHAR(30) NOT NULL DEFAULT 'QR_SCAN' 
                    CHECK (checkin_method IN ('QR_SCAN', 'MANUAL_ADMIN', 'RFID_BADGE')),
    device_ip       VARCHAR(50),
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    is_valid        BOOLEAN NOT NULL DEFAULT TRUE,
    notes           TEXT
);

-- -----------------------------------------------------------------------------
-- 12. VIOLATIONS (Lịch sử vi phạm quy chế & No-Show - UC12)
-- -----------------------------------------------------------------------------
CREATE TABLE violations (
    violation_id     VARCHAR(50) PRIMARY KEY,
    user_id          VARCHAR(50) NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    booking_id       VARCHAR(50) REFERENCES bookings(booking_id) ON DELETE SET NULL,
    violation_type   VARCHAR(30) NOT NULL 
                     CHECK (violation_type IN ('NO_SHOW', 'OVERSTAY', 'DAMAGE', 'UNAUTHORIZED_OCCUPY')),
    violation_date   DATE NOT NULL,
    penalty_applied  VARCHAR(50) NOT NULL DEFAULT 'WARNING_RECORDED',
    is_resolved      BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at      TIMESTAMPTZ,
    resolved_by      VARCHAR(50) REFERENCES users(user_id) ON DELETE SET NULL,
    resolution_notes TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_violations_user ON violations(user_id, is_resolved);

-- -----------------------------------------------------------------------------
-- 13. AUDIT_LOGS (Nhật ký kiểm toán an ninh hệ thống - UC08)
-- -----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    log_id          VARCHAR(50) PRIMARY KEY,
    actor_id        VARCHAR(50) REFERENCES users(user_id) ON DELETE SET NULL,
    category        VARCHAR(30) NOT NULL 
                    CHECK (category IN ('AUTH', 'SYS', 'ADMIN', 'BOOKING', 'CHECKIN', 'VIOLATION')),
    action          VARCHAR(50) NOT NULL,
    target_table    VARCHAR(50),
    target_id       VARCHAR(50),
    client_ip       VARCHAR(50),
    details_json    JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id, created_at DESC);
CREATE INDEX idx_audit_logs_category ON audit_logs(category, created_at DESC);

-- =============================================================================
-- DATABASE VIEWS (Báo cáo & Phân tích)
-- =============================================================================

-- View 1: Danh sách các đơn đặt chỗ đang hoạt động cùng đầy đủ thông tin phòng & người đặt
CREATE OR REPLACE VIEW v_active_bookings AS
SELECT 
    b.booking_id,
    b.booking_code,
    b.booking_date,
    ts.start_time,
    ts.end_time,
    ts.slot_label,
    r.resource_id,
    r.resource_name,
    r.resource_type,
    bld.building_name,
    flr.floor_name,
    u.user_id,
    u.employee_id,
    u.full_name AS employee_name,
    d.department_code,
    b.purpose,
    b.booking_status,
    b.created_at
FROM bookings b
JOIN users u ON b.user_id = u.user_id
JOIN departments d ON u.department_id = d.department_id
JOIN resources r ON b.resource_id = r.resource_id
JOIN buildings bld ON r.building_id = bld.building_id
JOIN floors flr ON r.floor_id = flr.floor_id
JOIN time_slots ts ON b.slot_id = ts.slot_id
WHERE b.booking_status IN ('CONFIRMED', 'CHECKED_IN');

-- View 2: Báo cáo tỷ lệ lấp đầy tài nguyên (Resource Utilization Rate)
CREATE OR REPLACE VIEW v_resource_utilization AS
SELECT 
    r.resource_id,
    r.resource_name,
    r.resource_type,
    bld.building_name,
    flr.floor_name,
    r.capacity,
    COUNT(b.booking_id) FILTER (WHERE b.booking_status IN ('CONFIRMED', 'CHECKED_IN', 'COMPLETED')) AS total_successful_bookings,
    COUNT(b.booking_id) FILTER (WHERE b.booking_status = 'NO_SHOW') AS total_no_shows,
    COUNT(b.booking_id) FILTER (WHERE b.booking_status = 'CANCELLED') AS total_cancellations
FROM resources r
JOIN buildings bld ON r.building_id = bld.building_id
JOIN floors flr ON r.floor_id = flr.floor_id
LEFT JOIN bookings b ON r.resource_id = b.resource_id
GROUP BY r.resource_id, r.resource_name, r.resource_type, bld.building_name, flr.floor_name, r.capacity;

-- View 3: Danh sách nhân viên có vi phạm & tình trạng tuân thủ quy chế (UC07, UC12)
CREATE OR REPLACE VIEW v_user_compliance_summary AS
SELECT 
    u.user_id,
    u.employee_id,
    u.full_name,
    u.email,
    d.department_code,
    d.department_name,
    u.violation_count,
    u.user_status,
    COUNT(v.violation_id) AS recorded_violations,
    MAX(v.created_at) AS last_violation_date
FROM users u
JOIN departments d ON u.department_id = d.department_id
LEFT JOIN violations v ON u.user_id = v.user_id
GROUP BY u.user_id, u.employee_id, u.full_name, u.email, d.department_code, d.department_name, u.violation_count, u.user_status;

-- =============================================================================
-- STORED PROCEDURES & ACID CONCURRENCY CONTROL
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Procedure 1: Đặt phòng / chỗ ngồi an toàn chống Race Condition (Double Booking)
-- Sử dụng Pessimistic Row Locking (FOR UPDATE) và kiểm tra trạng thái người dùng
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION sp_create_booking_safe(
    p_user_id       VARCHAR(50),
    p_resource_id   VARCHAR(50),
    p_slot_id       INT,
    p_booking_date  DATE,
    p_purpose       VARCHAR(255),
    p_attendee_cnt  INT
)
RETURNS TABLE (
    success         BOOLEAN,
    booking_id_out  VARCHAR(50),
    message_out     VARCHAR(255)
) 
LANGUAGE plpgsql
AS $$
DECLARE
    v_user_status   VARCHAR(30);
    v_violation_cnt INT;
    v_conflict_cnt  INT;
    v_new_id        VARCHAR(50);
    v_booking_code  VARCHAR(50);
    v_qr_hash       VARCHAR(255);
BEGIN
    -- 1. Kiểm tra trạng thái tài khoản người dùng
    SELECT user_status, violation_count INTO v_user_status, v_violation_cnt
    FROM users WHERE user_id = p_user_id;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, NULL::VARCHAR(50), 'Tài khoản người dùng không tồn tại.'::VARCHAR(255);
        RETURN;
    END IF;

    IF v_user_status = 'LOCKED' OR v_violation_cnt >= 3 THEN
        RETURN QUERY SELECT FALSE, NULL::VARCHAR(50), 'Tài khoản đã bị KHÓA do vi phạm quy chế check-in 3 lần.'::VARCHAR(255);
        RETURN;
    END IF;

    -- 2. Khóa dòng tài nguyên để tránh đồng thời 2 luồng cùng ghi (Pessimistic Locking)
    PERFORM resource_id FROM resources WHERE resource_id = p_resource_id FOR UPDATE;

    -- 3. Kiểm tra trùng lịch (Conflict Check)
    SELECT COUNT(*) INTO v_conflict_cnt
    FROM bookings
    WHERE resource_id = p_resource_id 
      AND booking_date = p_booking_date 
      AND slot_id = p_slot_id 
      AND booking_status NOT IN ('CANCELLED');

    IF v_conflict_cnt > 0 THEN
        RETURN QUERY SELECT FALSE, NULL::VARCHAR(50), 'Khung giờ này đã được đồng nghiệp khác đặt trước.'::VARCHAR(255);
        RETURN;
    END IF;

    -- 4. Tạo mã đơn đặt và mã băm QR Code
    v_new_id := 'BK-' || to_char(p_booking_date, 'YYYYMMDD') || '-' || substr(md5(random()::text), 1, 6);
    v_booking_code := 'FSP-' || upper(substr(md5(v_new_id || p_resource_id), 1, 8));
    v_qr_hash := encode(digest(v_new_id || '|' || p_resource_id || '|' || p_user_id, 'sha256'), 'hex');

    -- 5. Thực hiện thêm đơn đặt phòng (ACID Transaction)
    INSERT INTO bookings (
        booking_id, booking_code, user_id, resource_id, slot_id, 
        booking_date, purpose, attendee_count, qr_code_hash, booking_status
    ) VALUES (
        v_new_id, v_booking_code, p_user_id, p_resource_id, p_slot_id, 
        p_booking_date, p_purpose, p_attendee_cnt, v_qr_hash, 'CONFIRMED'
    );

    -- 6. Ghi nhật ký kiểm toán (Audit Trail)
    INSERT INTO audit_logs (
        log_id, actor_id, category, action, target_table, target_id, details_json
    ) VALUES (
        'log_' || substr(md5(random()::text), 1, 12),
        p_user_id,
        'BOOKING',
        'CREATE_BOOKING',
        'bookings',
        v_new_id,
        jsonb_build_object(
            'resource_id', p_resource_id,
            'slot_id', p_slot_id,
            'booking_date', p_booking_date,
            'purpose', p_purpose
        )
    );

    RETURN QUERY SELECT TRUE, v_new_id, 'Đặt chỗ làm việc thành công!'::VARCHAR(255);
END;
$$;

-- -----------------------------------------------------------------------------
-- Procedure 2: Quét mã QR Check-in điểm danh
-- Xác thực mã băm QR, cập nhật trạng thái đơn đặt và thêm vào bảng check_ins
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION sp_process_checkin(
    p_booking_id    VARCHAR(50),
    p_qr_hash       VARCHAR(255),
    p_device_ip     VARCHAR(50) DEFAULT '127.0.0.1'
)
RETURNS TABLE (
    success         BOOLEAN,
    message_out     VARCHAR(255)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status        VARCHAR(30);
    v_expected_hash VARCHAR(255);
    v_user_id       VARCHAR(50);
BEGIN
    SELECT booking_status, qr_code_hash, user_id
    INTO v_status, v_expected_hash, v_user_id
    FROM bookings
    WHERE booking_id = p_booking_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, 'Không tìm thấy thông tin đơn đặt chỗ.'::VARCHAR(255);
        RETURN;
    END IF;

    IF v_status = 'CHECKED_IN' THEN
        RETURN QUERY SELECT FALSE, 'Đơn đặt phòng này đã được điểm danh trước đó.'::VARCHAR(255);
        RETURN;
    END IF;

    IF v_status = 'CANCELLED' THEN
        RETURN QUERY SELECT FALSE, 'Đơn đặt phòng này đã bị hủy, không thể Check-in.'::VARCHAR(255);
        RETURN;
    END IF;

    IF v_status = 'NO_SHOW' THEN
        RETURN QUERY SELECT FALSE, 'Đơn đặt đã bị đánh dấu No-Show do quá thời gian cho phép.'::VARCHAR(255);
        RETURN;
    END IF;

    -- Xác thực mã QR Token
    IF p_qr_hash IS NOT NULL AND p_qr_hash != '' AND p_qr_hash != v_expected_hash THEN
        RETURN QUERY SELECT FALSE, 'Mã QR không hợp lệ hoặc đã bị can thiệp trái phép.'::VARCHAR(255);
        RETURN;
    END IF;

    -- Cập nhật đơn đặt thành CHECKED_IN
    UPDATE bookings 
    SET booking_status = 'CHECKED_IN', updated_at = CURRENT_TIMESTAMP
    WHERE booking_id = p_booking_id;

    -- Thêm bản ghi điểm danh
    INSERT INTO check_ins (
        checkin_id, booking_id, checked_in_at, checkin_method, device_ip, is_valid
    ) VALUES (
        'chk_' || substr(md5(random()::text), 1, 10),
        p_booking_id,
        CURRENT_TIMESTAMP,
        'QR_SCAN',
        p_device_ip,
        TRUE
    );

    -- Ghi nhật ký kiểm toán
    INSERT INTO audit_logs (
        log_id, actor_id, category, action, target_table, target_id, details_json
    ) VALUES (
        'log_' || substr(md5(random()::text), 1, 12),
        v_user_id,
        'CHECKIN',
        'QR_CHECKIN_SUCCESS',
        'bookings',
        p_booking_id,
        jsonb_build_object('checkin_method', 'QR_SCAN', 'device_ip', p_device_ip)
    );

    RETURN QUERY SELECT TRUE, 'Check-in thành công! Chúc bạn có buổi làm việc hiệu quả.'::VARCHAR(255);
END;
$$;

-- -----------------------------------------------------------------------------
-- Procedure 3: Xử lý tự động No-Show & Khóa tài khoản sau 3 lần (UC12)
-- Chạy theo định kỳ (Cron Job / Background Worker)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION sp_auto_process_noshow(
    p_target_date   DATE DEFAULT CURRENT_DATE
)
RETURNS TABLE (
    processed_count INT,
    users_locked    INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    r_booking       RECORD;
    v_processed     INT := 0;
    v_locked        INT := 0;
    v_new_violation INT;
BEGIN
    FOR r_booking IN 
        SELECT b.booking_id, b.user_id, b.resource_id, ts.end_time
        FROM bookings b
        JOIN time_slots ts ON b.slot_id = ts.slot_id
        WHERE b.booking_date = p_target_date
          AND b.booking_status = 'CONFIRMED'
          -- Điều kiện: Giờ hiện tại đã trôi qua thời gian bắt đầu ca họp + 15 phút
          AND CURRENT_TIME > (ts.start_time + INTERVAL '15 minutes')
    LOOP
        -- 1. Đổi trạng thái đơn đặt thành NO_SHOW
        UPDATE bookings 
        SET booking_status = 'NO_SHOW', updated_at = CURRENT_TIMESTAMP
        WHERE booking_id = r_booking.booking_id;

        -- 2. Tăng số lần vi phạm của người dùng
        UPDATE users 
        SET violation_count = violation_count + 1,
            user_status = CASE 
                WHEN violation_count + 1 >= 3 THEN 'LOCKED'
                ELSE 'WARNING'
            END,
            updated_at = CURRENT_TIMESTAMP
        WHERE user_id = r_booking.user_id
        RETURNING violation_count INTO v_new_violation;

        -- 3. Tạo bản ghi vi phạm vào bảng violations
        INSERT INTO violations (
            violation_id, user_id, booking_id, violation_type, violation_date, penalty_applied
        ) VALUES (
            'vio_' || substr(md5(random()::text), 1, 10),
            r_booking.user_id,
            r_booking.booking_id,
            'NO_SHOW',
            p_target_date,
            CASE WHEN v_new_violation >= 3 THEN 'ACCOUNT_LOCKED' ELSE 'WARNING_RECORDED' END
        );

        -- 4. Ghi nhật ký kiểm toán hệ thống
        INSERT INTO audit_logs (
            log_id, actor_id, category, action, target_table, target_id, details_json
        ) VALUES (
            'log_' || substr(md5(random()::text), 1, 12),
            r_booking.user_id,
            'VIOLATION',
            'AUTO_PENALTY_NOSHOW',
            'users',
            r_booking.user_id,
            jsonb_build_object(
                'booking_id', r_booking.booking_id,
                'new_violations', v_new_violation,
                'status_applied', CASE WHEN v_new_violation >= 3 THEN 'LOCKED' ELSE 'WARNING' END
            )
        );

        v_processed := v_processed + 1;
        IF v_new_violation >= 3 THEN
            v_locked := v_locked + 1;
        END IF;
    END LOOP;

    RETURN QUERY SELECT v_processed, v_locked;
END;
$$;

-- =============================================================================
-- SEED DATA (Dữ liệu mẫu thực tế FPT Software)
-- =============================================================================

-- 1. Roles
INSERT INTO roles (role_id, role_code, role_name, description) VALUES
(1, 'ADMIN', 'Quản trị viên hệ thống', 'Toàn quyền kiểm soát tài nguyên, mở khóa tài khoản, xem thống kê và quản trị nhật ký hệ thống.'),
(2, 'EMPLOYEE', 'Nhân viên FPT Software', 'Quyền tìm kiếm phòng, đặt chỗ, hủy lịch, check-in bằng mã QR và xem lịch sử.'),
(3, 'MANAGER', 'Quản lý Bộ Phận / FSU', 'Quyền duyệt phòng đặc biệt, xem báo cáo tuân thủ của nhân viên thuộc đơn vị.');

-- 2. Departments
INSERT INTO departments (department_id, department_code, department_name, campus) VALUES
(1, 'ADM.CORP', 'Ban Quản trị & Điều hành Văn phòng FPT', 'F-Town HCMC & FPT Tower HN'),
(2, 'FSU1.BU2', 'Trung tâm Phát triển Phần mềm Số 2 (Automotive & IoT)', 'F-Town 3, Khu Công nghệ cao TP.HCM'),
(3, 'FSU2.DX', 'Khối Chuyển đổi số & Trí tuệ nhân tạo (DX & AI)', 'F-Town 1, TP.Thủ Đức'),
(4, 'FSU1.BU9', 'Trung tâm Kiểm thử & Đảm bảo Chất lượng Phần mềm (QA/QC)', 'F-Town 2, TP.Thủ Đức'),
(5, 'FHN.FIN', 'Khối Tài chính Công nghệ FPT Software Hà Nội', 'FPT Tower Cầu Giấy, Hà Nội');

-- 3. Users
INSERT INTO users (user_id, employee_id, username, password_hash, email, phone, full_name, role_id, department_id, violation_count, user_status) VALUES
('usr_admin', 'ADM001', 'admin', crypt('admin123', gen_salt('bf')), 'admin@fpt.com', '0901234567', 'Hoàng Minh Tuấn', 1, 1, 0, 'ACTIVE'),
('usr_namtv', 'EMP302', 'employee', crypt('emp123', gen_salt('bf')), 'namtv@fpt.com', '0912345678', 'Trần Văn Nam', 2, 2, 0, 'ACTIVE'),
('usr_ducbt', 'EMP002', 'ducbt', crypt('123456', gen_salt('bf')), 'ducbt@fpt.com', '0987654321', 'Bùi Thế Đức', 2, 3, 4, 'LOCKED'),
('usr_maitt', 'EMP114', 'mai.tt', crypt('123456', gen_salt('bf')), 'mai.tt@fpt.com', '0977889900', 'Trương Thị Mai', 2, 4, 2, 'WARNING'),
('usr_linhdt', 'EMP505', 'linhdt', crypt('123456', gen_salt('bf')), 'linhdt@fpt.com', '0933221100', 'Đặng Thùy Linh', 2, 2, 0, 'ACTIVE');

-- 4. Buildings
INSERT INTO buildings (building_id, building_code, building_name, campus, address, total_floors) VALUES
('BLD_FTOWN1', 'FTOWN-1', 'Tòa nhà F-Town 1', 'Khu Công nghệ cao TP.HCM', 'Lô T2, Đường D1, Khu Công nghệ cao, TP.Thủ Đức, TP.HCM', 4),
('BLD_FTOWN2', 'FTOWN-2', 'Tòa nhà F-Town 2', 'Khu Công nghệ cao TP.HCM', 'Lô T2, Đường D1, Khu Công nghệ cao, TP.Thủ Đức, TP.HCM', 5),
('BLD_FTOWN3', 'FTOWN-3', 'Tòa nhà F-Town 3 (Campus Hiện Đại Nhất)', 'Khu Công nghệ cao TP.HCM', 'Đường D2, Khu Công nghệ cao, P.Tăng Nhơn Phú B, TP.Thủ Đức', 6),
('BLD_FPT_TWR', 'FPT-TOWER', 'Tòa nhà FPT Tower', 'Hà Nội Campus', 'Số 10 Phạm Văn Bạch, Dịch Vọng, Cầu Giấy, Hà Nội', 21);

-- 5. Floors
INSERT INTO floors (floor_id, building_id, floor_number, floor_name, floor_plan_asset) VALUES
('FLR_FT1_F1', 'BLD_FTOWN1', 1, 'Tầng 1 - Sảnh chính & Open Space', 'assets/map_ft1_f1.svg'),
('FLR_FT1_F2', 'BLD_FTOWN1', 2, 'Tầng 2 - Tech Scrum Hub', 'assets/map_ft1_f2.svg'),
('FLR_FT1_F3', 'BLD_FTOWN1', 3, 'Tầng 3 - Silent Workspace', 'assets/map_ft1_f3.svg'),
('FLR_FT2_F1', 'BLD_FTOWN2', 1, 'Tầng 1 - Innovation Hall', 'assets/map_ft2_f1.svg'),
('FLR_FT2_F2', 'BLD_FTOWN2', 2, 'Tầng 2 - Development Studio', 'assets/map_ft2_f2.svg'),
('FLR_FT3_F1', 'BLD_FTOWN3', 1, 'Tầng 1 - Hot Desk Hub & Phone Booths', 'assets/map_ft3_f1.svg'),
('FLR_FT3_F2', 'BLD_FTOWN3', 2, 'Tầng 2 - Executive & Huddle Rooms', 'assets/map_ft3_f2.svg'),
('FLR_FT3_F3', 'BLD_FTOWN3', 3, 'Tầng 3 - Gosling Conference Center', 'assets/map_ft3_f3.svg'),
('FLR_FT3_F4', 'BLD_FTOWN3', 4, 'Tầng 4 - Hardware Lab & Testing Zone', 'assets/map_ft3_f4.svg');

-- 6. Amenities
INSERT INTO amenities (amenity_id, amenity_code, amenity_name, icon_name, description) VALUES
(1, 'WIFI_6', 'Wi-Fi 6 Băng Thông Rộng', 'ic_wifi', 'Đường truyền cáp quang riêng biệt tốc độ cao, hỗ trợ VPN nội bộ.'),
(2, 'TV_4K_75', 'Màn Hình TV 4K 75 inch', 'ic_tv', 'Hỗ trợ kết nối không dây AirPlay & HDMI đa cổng.'),
(3, 'POLYCOM_VC', 'Hội Nghị Truyền Hình Polycom', 'ic_video_call', 'Hệ thống camera tracking thông minh và mic định hướng lọc ồn.'),
(4, 'GLASS_WHITEBOARD', 'Bảng Viết Kính Từ Tính', 'ic_board', 'Bảng kính từ tính cao cấp trang bị bút dạ nhiều màu và bông lau.'),
(5, 'AIR_CON_DAIKIN', 'Điều Hòa Trung Tâm Kháng Khuẩn', 'ic_ac', 'Nhiệt độ phòng luôn duy trì lý tưởng 22-24°C.'),
(6, 'ERGONOMIC_CHAIR', 'Ghế Công Thái Học Cao Cấp', 'ic_chair', 'Ghế bảo vệ cột sống Herman Miller / Sihoo cho tư thế ngồi làm việc chuẩn.'),
(7, 'POWER_USB_HUB', 'Ổ Cắm Đa Năng & Cổng Sạc USB-C', 'ic_power', 'Hỗ trợ sạc nhanh công suất cao cho Laptop và điện thoại.'),
(8, 'SOUNDPROOF', 'Vách Cách Âm Tiêu Chuẩn', 'ic_soundproof', 'Cách âm tiêu chuẩn phòng hội nghị, không bị vang vọng tiếng ồn.');

-- 7. Resources (Phòng họp & Chỗ ngồi)
INSERT INTO resources (resource_id, resource_code, resource_name, resource_type, building_id, floor_id, capacity, status, description) VALUES
('res_1', 'ROOM-GOSLING-FT3', 'Phòng họp Gosling', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F3', 12, 'AVAILABLE', 'Phòng họp cao cấp trang bị đầy đủ màn hình TV 4K, bảng kính, hệ thống mic họp trực tuyến Polycom chuyên nghiệp.'),
('res_2', 'ROOM-TURING-FT1', 'Phòng họp Turing', 'ROOM', 'BLD_FTOWN1', 'FLR_FT1_F2', 8, 'AVAILABLE', 'Thích hợp cho các buổi thảo luận nhóm kỹ thuật (scrum meetings). Không gian khép kín, cách âm tốt.'),
('res_3', 'ROOM-LOVELACE-FT3', 'Phòng họp Lovelace', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F2', 6, 'AVAILABLE', 'Không gian nhỏ ấm cúng cho các cuộc đối thoại 1-1 hoặc họp nhóm nhỏ. Có máy chiếu chất lượng cao.'),
('res_4', 'LAB-RITCHIE-FT2', 'Phòng Lab Ritchie', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F1', 15, 'AVAILABLE', 'Không gian nghiên cứu phát triển lớn phục vụ cho các dự án lab R&D. Thiết bị kiểm thử đầy đủ.'),
('res_5', 'ROOM-HOPPER-FT1', 'Phòng họp Hopper', 'ROOM', 'BLD_FTOWN1', 'FLR_FT1_F1', 4, 'AVAILABLE', 'Phòng họp nhanh không cần đặt trước lâu. Tiện nghi cơ bản đáp ứng thảo luận nhanh.'),
('res_6', 'DESK-A101-FT3', 'Bàn làm việc Desk-A101', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Chỗ ngồi làm việc cá nhân yên tĩnh, trang bị ghế công thái học Herman Miller thoải mái.'),
('res_7', 'DESK-A102-FT3', 'Bàn làm việc Desk-A102', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Bàn làm việc cá nhân nhìn ra giếng trời, không gian thoáng đãng đầy đủ ánh sáng tự nhiên.'),
('res_8', 'DESK-B201-FT1', 'Bàn làm việc Desk-B201', 'DESK', 'BLD_FTOWN1', 'FLR_FT1_F3', 1, 'AVAILABLE', 'Vị trí làm việc tập trung cao độ tại khu vực Silent Zone của F-Town 1.'),
('res_9', 'DESK-C301-FT2', 'Bàn làm việc Desk-C301', 'DESK', 'BLD_FTOWN2', 'FLR_FT2_F2', 1, 'AVAILABLE', 'Bàn làm việc nâng hạ độ cao điện tử thông minh (Standing Desk) trang bị màn hình ngoài 27 inch.'),
('res_10', 'BOOTH-1A-FT3', 'Phone Booth 1A', 'BOOTH', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Cabin cách âm tuyệt đối phục vụ phỏng vấn tuyển dụng hoặc trao đổi khách hàng bảo mật.'),
('res_11', 'HALL-DIJKSTRA-FT2', 'Hội trường Dijkstra', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F1', 50, 'AVAILABLE', 'Hội trường đào tạo và thuyết trình quy mô lớn, hệ thống âm thanh vòm và 2 máy chiếu 4K.'),
('res_12', 'BOOTH-1B-FT1', 'Phone Booth 1B', 'BOOTH', 'BLD_FTOWN1', 'FLR_FT1_F2', 1, 'AVAILABLE', 'Buồng điện thoại cách âm cạnh khu vực Pantry, yên tĩnh và riêng tư.'),
('res_13', 'TEAM-ALPHA-FT2', 'Team Space Alpha', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F2', 10, 'AVAILABLE', 'Phòng làm việc nhóm chuyên biệt dành cho các dự án Sprint cấp tốc, bảng trắng di động xung quanh.'),
('res_14', 'DESK-A103-FT3', 'Bàn làm việc Desk-A103', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Bàn làm việc cá nhân khu vực mở kết nối nhanh với khu pantry và cà phê.'),
('res_15', 'ROOM-KNUTH-FT3', 'Phòng họp Knuth', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F2', 4, 'AVAILABLE', 'Phòng họp nhỏ phục vụ trao đổi chuyên môn, kết nối video conference độ trễ thấp.');

-- 8. Resource Amenities Link
INSERT INTO resource_amenities (resource_id, amenity_id, quantity) VALUES
('res_1', 1, 1), ('res_1', 2, 1), ('res_1', 3, 1), ('res_1', 4, 1), ('res_1', 5, 1), ('res_1', 8, 1),
('res_2', 1, 1), ('res_2', 2, 1), ('res_2', 4, 1), ('res_2', 5, 1),
('res_3', 1, 1), ('res_3', 2, 1), ('res_3', 4, 1), ('res_3', 5, 1),
('res_4', 1, 1), ('res_4', 2, 2), ('res_4', 3, 1), ('res_4', 4, 2), ('res_4', 5, 2),
('res_5', 1, 1), ('res_5', 4, 1), ('res_5', 5, 1),
('res_6', 1, 1), ('res_6', 6, 1), ('res_6', 7, 1),
('res_7', 1, 1), ('res_7', 6, 1), ('res_7', 7, 1),
('res_8', 1, 1), ('res_8', 6, 1), ('res_8', 7, 1),
('res_9', 1, 1), ('res_9', 6, 1), ('res_9', 7, 1),
('res_10', 1, 1), ('res_10', 7, 1), ('res_10', 8, 1),
('res_11', 1, 1), ('res_11', 2, 2), ('res_11', 3, 1), ('res_11', 5, 2), ('res_11', 8, 1),
('res_12', 1, 1), ('res_12', 7, 1), ('res_12', 8, 1),
('res_13', 1, 1), ('res_13', 2, 1), ('res_13', 4, 2), ('res_13', 5, 1),
('res_14', 1, 1), ('res_14', 6, 1), ('res_14', 7, 1),
('res_15', 1, 1), ('res_15', 2, 1), ('res_15', 3, 1), ('res_15', 5, 1);

-- 9. Time Slots
INSERT INTO time_slots (slot_id, slot_code, start_time, end_time, slot_label) VALUES
(1, 'SLOT_01', '08:00', '10:00', 'Khung giờ 1 (08:00 - 10:00)'),
(2, 'SLOT_02', '10:00', '12:00', 'Khung giờ 2 (10:00 - 12:00)'),
(3, 'SLOT_03', '13:00', '15:00', 'Khung giờ 3 (13:00 - 15:00)'),
(4, 'SLOT_04', '15:00', '17:00', 'Khung giờ 4 (15:00 - 17:00)'),
(5, 'SLOT_05', '18:00', '20:00', 'Khung giờ 5 (18:00 - 20:00)');

-- 10. Sample Bookings (Đa dạng trạng thái và người dùng)
INSERT INTO bookings (booking_id, booking_code, user_id, resource_id, slot_id, booking_date, purpose, attendee_count, qr_code_hash, booking_status) VALUES
('BK-20261020-001', 'FSP-GOSLING-01', 'usr_namtv', 'res_1', 1, CURRENT_DATE, 'Họp Kick-off Dự án FPT Connected Vehicle', 8, encode(digest('QR_res_1_slot1', 'sha256'), 'hex'), 'CHECKED_IN'),
('BK-20261020-002', 'FSP-TURING-02', 'usr_namtv', 'res_2', 3, CURRENT_DATE, 'Sprint Retrospective BU2 - Đánh giá Sprint 24', 6, encode(digest('QR_res_2_slot3', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261020-003', 'FSP-DESK-03', 'usr_namtv', 'res_6', 2, CURRENT_DATE, 'Làm việc tập trung phân tích kiến trúc phần mềm', 1, encode(digest('QR_res_6_slot2', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261020-004', 'FSP-ALPHA-04', 'usr_namtv', 'res_13', 4, CURRENT_DATE, 'Workshop Kiến trúc Microservices cho team Dev', 10, encode(digest('QR_res_13_slot4', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261021-001', 'FSP-RITCHIE-05', 'usr_namtv', 'res_4', 2, CURRENT_DATE + 1, 'Kiểm thử tích hợp phần cứng cảm biến IoT trên xe', 12, encode(digest('QR_res_4_slot2', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261021-002', 'FSP-BOOTH-06', 'usr_linhdt', 'res_10', 1, CURRENT_DATE + 1, 'Phỏng vấn ứng viên Senior UI/UX Designer', 2, encode(digest('QR_res_10_slot1', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261022-001', 'FSP-DIJKSTRA-07', 'usr_admin', 'res_11', 3, CURRENT_DATE + 2, 'Town Hall toàn FSU: Tổng kết quý và định hướng công nghệ', 45, encode(digest('QR_res_11_slot3', 'sha256'), 'hex'), 'CONFIRMED'),
('BK-20261019-001', 'FSP-DUC-NOSHOW1', 'usr_ducbt', 'res_3', 2, CURRENT_DATE - 1, 'Họp kỹ thuật DX Platform với khách hàng Nhật Bản', 4, encode(digest('QR_res_3_slot2', 'sha256'), 'hex'), 'NO_SHOW'),
('BK-20261018-001', 'FSP-DUC-NOSHOW2', 'usr_ducbt', 'res_1', 4, CURRENT_DATE - 2, 'Phỏng vấn ứng viên AI Engineer', 3, encode(digest('QR_res_1_slot4', 'sha256'), 'hex'), 'NO_SHOW'),
('BK-20261017-001', 'FSP-MAI-NOSHOW1', 'usr_maitt', 'res_2', 1, CURRENT_DATE - 3, 'Họp phân công test suite tự động kiểm thử hiệu năng', 5, encode(digest('QR_res_2_slot1', 'sha256'), 'hex'), 'NO_SHOW'),
('BK-20261016-001', 'FSP-NAM-CANCEL', 'usr_namtv', 'res_5', 5, CURRENT_DATE - 4, 'Họp đột xuất giải quyết sự cố server staging', 3, encode(digest('QR_res_5_slot5', 'sha256'), 'hex'), 'CANCELLED');

-- 11. Sample Check-ins
INSERT INTO check_ins (checkin_id, booking_id, checked_in_at, checkin_method, device_ip, is_valid, notes) VALUES
('chk_001', 'BK-20261020-001', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'QR_SCAN', '10.20.14.88', TRUE, 'Check-in thành công tại cửa phòng Gosling Tầng 3 F-Town 3.');

-- 12. Sample Violations (UC12)
INSERT INTO violations (violation_id, user_id, booking_id, violation_type, violation_date, penalty_applied, is_resolved) VALUES
('vio_001', 'usr_ducbt', 'BK-20261019-001', 'NO_SHOW', CURRENT_DATE - 1, 'ACCOUNT_LOCKED', FALSE),
('vio_002', 'usr_ducbt', 'BK-20261018-001', 'NO_SHOW', CURRENT_DATE - 2, 'WARNING_RECORDED', FALSE),
('vio_003', 'usr_maitt', 'BK-20261017-001', 'NO_SHOW', CURRENT_DATE - 3, 'WARNING_RECORDED', FALSE);

-- 13. Sample Audit Logs (UC08)
INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json) VALUES
('log_001', 'usr_admin', 'SYS', 'INIT_DATABASE', 'system', 'v2.0', '{"message": "Cơ sở dữ liệu F-Space Booking Enterprise V2 khởi tạo thành công"}'::jsonb),
('log_002', 'usr_namtv', 'AUTH', 'USER_LOGIN', 'users', 'usr_namtv', '{"device": "Samsung Galaxy S24 Ultra", "ip": "10.20.14.88"}'::jsonb),
('log_003', 'usr_namtv', 'BOOKING', 'CREATE_BOOKING', 'bookings', 'BK-20261020-001', '{"resource_name": "Phòng họp Gosling", "slot": "08:00 - 10:00"}'::jsonb),
('log_004', 'usr_namtv', 'CHECKIN', 'QR_CHECKIN_SUCCESS', 'check_ins', 'chk_001', '{"method": "QR_CAMERA_SCAN"}'::jsonb),
('log_005', 'usr_admin', 'ADMIN', 'LOCK_USER_ACCOUNT', 'users', 'usr_ducbt', '{"reason": "Tự động khóa sau 3 lần No-Show theo quy chế UC12"}'::jsonb);

-- =============================================================================
-- END OF SCHEMA SCRIPT
-- =============================================================================
