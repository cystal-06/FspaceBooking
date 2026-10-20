-- =============================================================================
-- F-SPACE WORKSPACE & MEETING ROOM BOOKING MANAGEMENT SYSTEM
-- READY-TO-VIEW DATABASE SCHEMA (SQLite / DBeaver)
-- Project: Đồ Án Tốt Nghiệp / Graduation Thesis
-- Enterprise: FPT Software (F-Town Campuses & FPT Tower)
-- =============================================================================

PRAGMA foreign_keys = ON;

-- -----------------------------------------------------------------------------
-- 0. CLEANUP
-- -----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_user_compliance_summary;
DROP VIEW IF EXISTS v_resource_utilization;
DROP VIEW IF EXISTS v_active_bookings;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS violations;
DROP TABLE IF EXISTS check_ins;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS time_slots;
DROP TABLE IF EXISTS resource_amenities;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS amenities;
DROP TABLE IF EXISTS floors;
DROP TABLE IF EXISTS buildings;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS roles;

-- -----------------------------------------------------------------------------
-- 1. ROLES & PERMISSIONS
-- -----------------------------------------------------------------------------
CREATE TABLE roles (
    role_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    role_code       TEXT UNIQUE NOT NULL, -- 'ADMIN', 'EMPLOYEE', 'MANAGER'
    role_name       TEXT NOT NULL,
    description     TEXT,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 2. DEPARTMENTS / FSU
-- -----------------------------------------------------------------------------
CREATE TABLE departments (
    department_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    department_code TEXT UNIQUE NOT NULL, -- 'FSU1.BU2', 'FSU2.DX', 'FHN.FIN'
    department_name TEXT NOT NULL,
    campus          TEXT NOT NULL DEFAULT 'F-Town HCMC',
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 3. USERS & IDENTITY
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    user_id         TEXT PRIMARY KEY,
    employee_id     TEXT UNIQUE NOT NULL, -- 'ADM001', 'EMP302', 'EMP002'
    username        TEXT UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
    email           TEXT UNIQUE NOT NULL,
    phone           TEXT,
    full_name       TEXT NOT NULL,
    role_id         INTEGER NOT NULL REFERENCES roles(role_id) ON DELETE RESTRICT,
    department_id   INTEGER NOT NULL REFERENCES departments(department_id) ON DELETE RESTRICT,
    violation_count INTEGER NOT NULL DEFAULT 0,
    user_status     TEXT NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'WARNING', 'LOCKED'
    avatar_url      TEXT,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status_violations ON users(user_status, violation_count);

-- -----------------------------------------------------------------------------
-- 4. BUILDINGS & CAMPUSES
-- -----------------------------------------------------------------------------
CREATE TABLE buildings (
    building_id     TEXT PRIMARY KEY,
    building_code   TEXT UNIQUE NOT NULL,
    building_name   TEXT NOT NULL,
    campus          TEXT NOT NULL,
    address         TEXT NOT NULL,
    total_floors    INTEGER NOT NULL DEFAULT 1,
    is_active       INTEGER NOT NULL DEFAULT 1,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 5. FLOORS
-- -----------------------------------------------------------------------------
CREATE TABLE floors (
    floor_id        TEXT PRIMARY KEY,
    building_id     TEXT NOT NULL REFERENCES buildings(building_id) ON DELETE CASCADE,
    floor_number    INTEGER NOT NULL,
    floor_name      TEXT NOT NULL,
    floor_plan_asset TEXT,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (building_id, floor_number)
);

-- -----------------------------------------------------------------------------
-- 6. AMENITIES
-- -----------------------------------------------------------------------------
CREATE TABLE amenities (
    amenity_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    amenity_code    TEXT UNIQUE NOT NULL,
    amenity_name    TEXT NOT NULL,
    icon_name       TEXT NOT NULL,
    description     TEXT
);

-- -----------------------------------------------------------------------------
-- 7. WORKSPACE RESOURCES (Phòng họp & Bàn làm việc)
-- -----------------------------------------------------------------------------
CREATE TABLE resources (
    resource_id     TEXT PRIMARY KEY,
    resource_code   TEXT UNIQUE NOT NULL,
    resource_name   TEXT NOT NULL,
    resource_type   TEXT NOT NULL, -- 'ROOM', 'DESK', 'LAB', 'BOOTH'
    building_id     TEXT NOT NULL REFERENCES buildings(building_id) ON DELETE RESTRICT,
    floor_id        TEXT NOT NULL REFERENCES floors(floor_id) ON DELETE RESTRICT,
    capacity        INTEGER NOT NULL DEFAULT 1,
    status          TEXT NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'BOOKED', 'MAINTENANCE'
    description     TEXT,
    image_url       TEXT,
    is_active       INTEGER NOT NULL DEFAULT 1,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resources_search ON resources(resource_type, status, building_id);

-- -----------------------------------------------------------------------------
-- 8. RESOURCE_AMENITIES (N:N)
-- -----------------------------------------------------------------------------
CREATE TABLE resource_amenities (
    resource_id     TEXT NOT NULL REFERENCES resources(resource_id) ON DELETE CASCADE,
    amenity_id      INTEGER NOT NULL REFERENCES amenities(amenity_id) ON DELETE CASCADE,
    quantity        INTEGER NOT NULL DEFAULT 1,
    notes           TEXT,
    PRIMARY KEY (resource_id, amenity_id)
);

-- -----------------------------------------------------------------------------
-- 9. TIME SLOTS
-- -----------------------------------------------------------------------------
CREATE TABLE time_slots (
    slot_id         INTEGER PRIMARY KEY,
    slot_code       TEXT UNIQUE NOT NULL,
    start_time      TEXT NOT NULL,
    end_time        TEXT NOT NULL,
    slot_label      TEXT NOT NULL,
    is_active       INTEGER NOT NULL DEFAULT 1
);

-- -----------------------------------------------------------------------------
-- 10. BOOKINGS (Đơn đặt phòng / chỗ ngồi)
-- -----------------------------------------------------------------------------
CREATE TABLE bookings (
    booking_id          TEXT PRIMARY KEY,
    booking_code        TEXT UNIQUE NOT NULL,
    user_id             TEXT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    resource_id         TEXT NOT NULL REFERENCES resources(resource_id) ON DELETE RESTRICT,
    slot_id             INTEGER NOT NULL REFERENCES time_slots(slot_id) ON DELETE RESTRICT,
    booking_date        TEXT NOT NULL,
    purpose             TEXT NOT NULL DEFAULT 'Họp công việc nội bộ',
    attendee_count      INTEGER NOT NULL DEFAULT 1,
    qr_code_hash        TEXT NOT NULL,
    booking_status      TEXT NOT NULL DEFAULT 'CONFIRMED', -- 'CONFIRMED', 'CHECKED_IN', 'CANCELLED', 'NO_SHOW'
    created_at          TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at        TEXT,
    cancellation_reason TEXT
);

-- ANTI-DOUBLE BOOKING INDEX
CREATE UNIQUE INDEX uq_anti_double_booking 
ON bookings (resource_id, booking_date, slot_id) 
WHERE booking_status != 'CANCELLED';

CREATE INDEX idx_bookings_user_date ON bookings(user_id, booking_date);

-- -----------------------------------------------------------------------------
-- 11. CHECK_INS (Điểm danh QR)
-- -----------------------------------------------------------------------------
CREATE TABLE check_ins (
    checkin_id      TEXT PRIMARY KEY,
    booking_id      TEXT UNIQUE NOT NULL REFERENCES bookings(booking_id) ON DELETE CASCADE,
    checked_in_at   TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checkin_method  TEXT NOT NULL DEFAULT 'QR_SCAN',
    device_ip       TEXT,
    is_valid        INTEGER NOT NULL DEFAULT 1,
    notes           TEXT
);

-- -----------------------------------------------------------------------------
-- 12. VIOLATIONS (Lịch sử No-Show - UC12)
-- -----------------------------------------------------------------------------
CREATE TABLE violations (
    violation_id     TEXT PRIMARY KEY,
    user_id          TEXT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    booking_id       TEXT REFERENCES bookings(booking_id) ON DELETE SET NULL,
    violation_type   TEXT NOT NULL, -- 'NO_SHOW', 'OVERSTAY'
    violation_date   TEXT NOT NULL,
    penalty_applied  TEXT NOT NULL DEFAULT 'WARNING_RECORDED',
    is_resolved      INTEGER NOT NULL DEFAULT 0,
    resolved_at      TEXT,
    resolution_notes TEXT,
    created_at       TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 13. AUDIT_LOGS (Nhật ký kiểm toán an ninh - UC08)
-- -----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    log_id          TEXT PRIMARY KEY,
    actor_id        TEXT REFERENCES users(user_id) ON DELETE SET NULL,
    category        TEXT NOT NULL, -- 'AUTH', 'SYS', 'ADMIN', 'BOOKING', 'CHECKIN', 'VIOLATION'
    action          TEXT NOT NULL,
    target_table    TEXT,
    target_id       TEXT,
    client_ip       TEXT,
    details_json    TEXT,
    created_at      TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- DATABASE VIEWS
-- =============================================================================

CREATE VIEW v_active_bookings AS
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

CREATE VIEW v_user_compliance_summary AS
SELECT 
    u.user_id,
    u.employee_id,
    u.full_name,
    u.email,
    d.department_code,
    d.department_name,
    u.violation_count,
    u.user_status,
    COUNT(v.violation_id) AS recorded_violations
FROM users u
JOIN departments d ON u.department_id = d.department_id
LEFT JOIN violations v ON u.user_id = v.user_id
GROUP BY u.user_id, u.employee_id, u.full_name, u.email, d.department_code, d.department_name, u.violation_count, u.user_status;

-- =============================================================================
-- SEED DATA (FPT Software)
-- =============================================================================

INSERT INTO roles (role_id, role_code, role_name, description) VALUES
(1, 'ADMIN', 'Quản trị viên hệ thống', 'Toàn quyền kiểm soát tài nguyên, mở khóa tài khoản, xem thống kê và nhật ký.'),
(2, 'EMPLOYEE', 'Nhân viên FPT Software', 'Quyền tìm kiếm phòng, đặt chỗ, hủy lịch, check-in bằng mã QR và xem lịch sử.'),
(3, 'MANAGER', 'Quản lý Bộ Phận / FSU', 'Quyền duyệt phòng đặc biệt, xem báo cáo tuân thủ của nhân viên thuộc đơn vị.');

INSERT INTO departments (department_id, department_code, department_name, campus) VALUES
(1, 'ADM.CORP', 'Ban Quản trị & Điều hành Văn phòng FPT', 'F-Town HCMC & FPT Tower HN'),
(2, 'FSU1.BU2', 'Trung tâm Phát triển Phần mềm Số 2 (Automotive & IoT)', 'F-Town 3, Khu Công nghệ cao TP.HCM'),
(3, 'FSU2.DX', 'Khối Chuyển đổi số & Trí tuệ nhân tạo (DX & AI)', 'F-Town 1, TP.Thủ Đức'),
(4, 'FSU1.BU9', 'Trung tâm Kiểm thử & Đảm bảo Chất lượng Phần mềm (QA/QC)', 'F-Town 2, TP.Thủ Đức'),
(5, 'FHN.FIN', 'Khối Tài chính Công nghệ FPT Software Hà Nội', 'FPT Tower Cầu Giấy, Hà Nội');

INSERT INTO users (user_id, employee_id, username, password_hash, email, phone, full_name, role_id, department_id, violation_count, user_status) VALUES
('usr_admin', 'ADM001', 'admin', 'admin123', 'admin@fpt.com', '0901234567', 'Hoàng Minh Tuấn', 1, 1, 0, 'ACTIVE'),
('usr_namtv', 'EMP302', 'employee', 'emp123', 'namtv@fpt.com', '0912345678', 'Trần Văn Nam', 2, 2, 0, 'ACTIVE'),
('usr_ducbt', 'EMP002', 'ducbt', '123456', 'ducbt@fpt.com', '0987654321', 'Bùi Thế Đức', 2, 3, 4, 'LOCKED'),
('usr_maitt', 'EMP114', 'mai.tt', '123456', 'mai.tt@fpt.com', '0977889900', 'Trương Thị Mai', 2, 4, 2, 'WARNING'),
('usr_linhdt', 'EMP505', 'linhdt', '123456', 'linhdt@fpt.com', '0933221100', 'Đặng Thùy Linh', 2, 2, 0, 'ACTIVE');

INSERT INTO buildings (building_id, building_code, building_name, campus, address, total_floors) VALUES
('BLD_FTOWN1', 'FTOWN-1', 'Tòa nhà F-Town 1', 'Khu Công nghệ cao TP.HCM', 'Lô T2, Đường D1, Khu Công nghệ cao, TP.Thủ Đức, TP.HCM', 4),
('BLD_FTOWN2', 'FTOWN-2', 'Tòa nhà F-Town 2', 'Khu Công nghệ cao TP.HCM', 'Lô T2, Đường D1, Khu Công nghệ cao, TP.Thủ Đức, TP.HCM', 5),
('BLD_FTOWN3', 'FTOWN-3', 'Tòa nhà F-Town 3 (Campus Hiện Đại Nhất)', 'Khu Công nghệ cao TP.HCM', 'Đường D2, Khu Công nghệ cao, P.Tăng Nhơn Phú B, TP.Thủ Đức', 6),
('BLD_FPT_TWR', 'FPT-TOWER', 'Tòa nhà FPT Tower', 'Hà Nội Campus', 'Số 10 Phạm Văn Bạch, Dịch Vọng, Cầu Giấy, Hà Nội', 21);

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

INSERT INTO amenities (amenity_id, amenity_code, amenity_name, icon_name, description) VALUES
(1, 'WIFI_6', 'Wi-Fi 6 Băng Thông Rộng', 'ic_wifi', 'Đường truyền cáp quang riêng biệt tốc độ cao, hỗ trợ VPN nội bộ.'),
(2, 'TV_4K_75', 'Màn Hình TV 4K 75 inch', 'ic_tv', 'Hỗ trợ kết nối không dây AirPlay & HDMI đa cổng.'),
(3, 'POLYCOM_VC', 'Hội Nghị Truyền Hình Polycom', 'ic_video_call', 'Hệ thống camera tracking thông minh và mic định hướng lọc ồn.'),
(4, 'GLASS_WHITEBOARD', 'Bảng Viết Kính Từ Tính', 'ic_board', 'Bảng kính từ tính cao cấp trang bị bút dạ nhiều màu và bông lau.'),
(5, 'AIR_CON_DAIKIN', 'Điều Hòa Trung Tâm Kháng Khuẩn', 'ic_ac', 'Nhiệt độ phòng luôn duy trì lý tưởng 22-24°C.'),
(6, 'ERGONOMIC_CHAIR', 'Ghế Công Thái Học Cao Cấp', 'ic_chair', 'Ghế bảo vệ cột sống Herman Miller cho tư thế ngồi làm việc chuẩn.'),
(7, 'POWER_USB_HUB', 'Ổ Cắm Đa Năng & Cổng Sạc USB-C', 'ic_power', 'Hỗ trợ sạc nhanh công suất cao cho Laptop và điện thoại.'),
(8, 'SOUNDPROOF', 'Vách Cách Âm Tiêu Chuẩn', 'ic_soundproof', 'Cách âm tiêu chuẩn phòng hội nghị, không bị vang vọng tiếng ồn.');

INSERT INTO resources (resource_id, resource_code, resource_name, resource_type, building_id, floor_id, capacity, status, description) VALUES
('res_1', 'ROOM-GOSLING-FT3', 'Phòng họp Gosling', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F3', 12, 'AVAILABLE', 'Phòng họp cao cấp trang bị đầy đủ màn hình TV 4K, bảng kính, hệ thống mic họp trực tuyến Polycom chuyên nghiệp.'),
('res_2', 'ROOM-TURING-FT1', 'Phòng họp Turing', 'ROOM', 'BLD_FTOWN1', 'FLR_FT1_F2', 8, 'AVAILABLE', 'Thích hợp cho các buổi thảo luận nhóm kỹ thuật (scrum meetings). Không gian khép kín, cách âm tốt.'),
('res_3', 'ROOM-LOVELACE-FT3', 'Phòng họp Lovelace', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F2', 6, 'AVAILABLE', 'Không gian nhỏ ấm cúng cho các cuộc đối thoại 1-1 hoặc họp nhóm nhỏ. Có máy chiếu chất lượng cao.'),
('res_4', 'LAB-RITCHIE-FT2', 'Phòng Lab Ritchie', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F1', 15, 'AVAILABLE', 'Không gian nghiên cứu phát triển lớn phục vụ cho các dự án lab R&D. Thiết bị kiểm thử đầy đủ.'),
('res_5', 'ROOM-HOPPER-FT1', 'Phòng họp Hopper', 'ROOM', 'BLD_FTOWN1', 'FLR_FT1_F1', 4, 'AVAILABLE', 'Phòng họp nhanh không cần đặt trước lâu. Tiện nghi cơ bản đáp ứng thảo luận nhanh.'),
('res_6', 'DESK-A101-FT3', 'Bàn làm việc cá nhân A-101', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Chỗ ngồi làm việc cá nhân yên tĩnh, trang bị ghế công thái học Herman Miller thoải mái.'),
('res_7', 'DESK-A102-FT3', 'Bàn làm việc cá nhân A-102', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Bàn làm việc cá nhân nhìn ra giếng trời, không gian thoáng đãng đầy đủ ánh sáng tự nhiên.'),
('res_8', 'DESK-B201-FT1', 'Bàn làm việc B-201', 'DESK', 'BLD_FTOWN1', 'FLR_FT1_F3', 1, 'AVAILABLE', 'Vị trí làm việc tập trung cao độ tại khu vực Silent Zone của F-Town 1.'),
('res_9', 'DESK-C301-FT2', 'Bàn làm việc nâng hạ C-301', 'DESK', 'BLD_FTOWN2', 'FLR_FT2_F2', 1, 'AVAILABLE', 'Bàn làm việc nâng hạ độ cao điện tử thông minh (Standing Desk) trang bị màn hình ngoài 27 inch.'),
('res_10', 'BOOTH-1A-FT3', 'Buồng điện thoại cách âm 1A', 'BOOTH', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Cabin cách âm tuyệt đối phục vụ phỏng vấn tuyển dụng hoặc trao đổi khách hàng bảo mật.'),
('res_11', 'HALL-DIJKSTRA-FT2', 'Hội trường Dijkstra', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F1', 50, 'AVAILABLE', 'Hội trường đào tạo và thuyết trình quy mô lớn, hệ thống âm thanh vòm và 2 máy chiếu 4K.'),
('res_12', 'BOOTH-1B-FT1', 'Buồng điện thoại cách âm 1B', 'BOOTH', 'BLD_FTOWN1', 'FLR_FT1_F2', 1, 'AVAILABLE', 'Buồng điện thoại cách âm cạnh khu vực Pantry, yên tĩnh và riêng tư.'),
('res_13', 'TEAM-ALPHA-FT2', 'Phòng làm việc nhóm Alpha', 'ROOM', 'BLD_FTOWN2', 'FLR_FT2_F2', 10, 'AVAILABLE', 'Phòng làm việc nhóm chuyên biệt dành cho các dự án Sprint cấp tốc, bảng trắng di động xung quanh.'),
('res_14', 'DESK-A103-FT3', 'Bàn làm việc A-103', 'DESK', 'BLD_FTOWN3', 'FLR_FT3_F1', 1, 'AVAILABLE', 'Bàn làm việc cá nhân khu vực mở kết nối nhanh với khu pantry và cà phê.'),
('res_15', 'ROOM-KNUTH-FT3', 'Phòng họp Knuth', 'ROOM', 'BLD_FTOWN3', 'FLR_FT3_F2', 4, 'AVAILABLE', 'Phòng họp nhỏ phục vụ trao đổi chuyên môn, kết nối video conference độ trễ thấp.');

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

INSERT INTO time_slots (slot_id, slot_code, start_time, end_time, slot_label) VALUES
(1, 'SLOT_01', '08:00', '10:00', 'Ca 1 (08:00 - 10:00)'),
(2, 'SLOT_02', '10:00', '12:00', 'Ca 2 (10:00 - 12:00)'),
(3, 'SLOT_03', '13:00', '15:00', 'Ca 3 (13:00 - 15:00)'),
(4, 'SLOT_04', '15:00', '17:00', 'Ca 4 (15:00 - 17:00)'),
(5, 'SLOT_05', '18:00', '20:00', 'Ca 5 (18:00 - 20:00)');

INSERT INTO bookings (booking_id, booking_code, user_id, resource_id, slot_id, booking_date, purpose, attendee_count, qr_code_hash, booking_status) VALUES
('BK-20261020-001', 'FSP-GOSLING-01', 'usr_namtv', 'res_1', 1, '2026-10-20', 'Họp Kick-off Dự án FPT Connected Vehicle', 8, 'QR_HASH_GOSLING_01', 'CHECKED_IN'),
('BK-20261020-002', 'FSP-TURING-02', 'usr_namtv', 'res_2', 3, '2026-10-20', 'Sprint Retrospective BU2 - Đánh giá Sprint 24', 6, 'QR_HASH_TURING_02', 'CONFIRMED'),
('BK-20261020-003', 'FSP-DESK-03', 'usr_namtv', 'res_6', 2, '2026-10-20', 'Làm việc tập trung phân tích kiến trúc phần mềm', 1, 'QR_HASH_DESK_03', 'CONFIRMED'),
('BK-20261020-004', 'FSP-ALPHA-04', 'usr_namtv', 'res_13', 4, '2026-10-20', 'Workshop Kiến trúc Microservices cho team Dev', 10, 'QR_HASH_ALPHA_04', 'CONFIRMED'),
('BK-20261021-001', 'FSP-RITCHIE-05', 'usr_namtv', 'res_4', 2, '2026-10-21', 'Kiểm thử tích hợp phần cứng cảm biến IoT trên xe', 12, 'QR_HASH_RITCHIE_05', 'CONFIRMED'),
('BK-20261021-002', 'FSP-BOOTH-06', 'usr_linhdt', 'res_10', 1, '2026-10-21', 'Phỏng vấn ứng viên Senior UI/UX Designer', 2, 'QR_HASH_BOOTH_06', 'CONFIRMED'),
('BK-20261022-001', 'FSP-DIJKSTRA-07', 'usr_admin', 'res_11', 3, '2026-10-22', 'Town Hall toàn FSU: Tổng kết quý và định hướng công nghệ', 45, 'QR_HASH_DIJKSTRA_07', 'CONFIRMED'),
('BK-20261019-001', 'FSP-DUC-NOSHOW1', 'usr_ducbt', 'res_3', 2, '2026-10-19', 'Họp kỹ thuật DX Platform với khách hàng Nhật Bản', 4, 'QR_HASH_DUC_01', 'NO_SHOW'),
('BK-20261018-001', 'FSP-DUC-NOSHOW2', 'usr_ducbt', 'res_1', 4, '2026-10-18', 'Phỏng vấn ứng viên AI Engineer', 3, 'QR_HASH_DUC_02', 'NO_SHOW'),
('BK-20261017-001', 'FSP-MAI-NOSHOW1', 'usr_maitt', 'res_2', 1, '2026-10-17', 'Họp phân công test suite tự động kiểm thử hiệu năng', 5, 'QR_HASH_MAI_01', 'NO_SHOW'),
('BK-20261016-001', 'FSP-NAM-CANCEL', 'usr_namtv', 'res_5', 5, '2026-10-16', 'Họp đột xuất giải quyết sự cố server staging', 3, 'QR_HASH_NAM_CANCEL', 'CANCELLED');

INSERT INTO check_ins (checkin_id, booking_id, checked_in_at, checkin_method, device_ip, is_valid, notes) VALUES
('chk_001', 'BK-20261020-001', '2026-10-20 08:05:00', 'QR_SCAN', '10.20.14.88', 1, 'Check-in thành công tại cửa phòng Gosling Tầng 3 F-Town 3.');

INSERT INTO violations (violation_id, user_id, booking_id, violation_type, violation_date, penalty_applied, is_resolved) VALUES
('vio_001', 'usr_ducbt', 'BK-20261019-001', 'NO_SHOW', '2026-10-19', 'ACCOUNT_LOCKED', 0),
('vio_002', 'usr_ducbt', 'BK-20261018-001', 'NO_SHOW', '2026-10-18', 'WARNING_RECORDED', 0),
('vio_003', 'usr_maitt', 'BK-20261017-001', 'NO_SHOW', '2026-10-17', 'WARNING_RECORDED', 0);

INSERT INTO audit_logs (log_id, actor_id, category, action, target_table, target_id, details_json) VALUES
('log_001', 'usr_admin', 'SYS', 'INIT_DATABASE', 'system', 'v2.0', '{"message": "Cơ sở dữ liệu F-Space Booking Enterprise khởi tạo thành công"}'),
('log_002', 'usr_namtv', 'AUTH', 'USER_LOGIN', 'users', 'usr_namtv', '{"device": "Samsung Galaxy S24 Ultra", "ip": "10.20.14.88"}'),
('log_003', 'usr_namtv', 'BOOKING', 'CREATE_BOOKING', 'bookings', 'BK-20261020-001', '{"resource_name": "Phòng họp Gosling", "slot": "08:00 - 10:00"}'),
('log_004', 'usr_namtv', 'CHECKIN', 'QR_CHECKIN_SUCCESS', 'check_ins', 'chk_001', '{"method": "QR_CAMERA_SCAN"}'),
('log_005', 'usr_admin', 'ADMIN', 'LOCK_USER_ACCOUNT', 'users', 'usr_ducbt', '{"reason": "Tự động khóa sau 3 lần No-Show theo quy chế UC12"}');
