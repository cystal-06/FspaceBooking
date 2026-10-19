# F-Space Booking - Quản Lý & Đặt Trước Tài Nguyên Doanh Nghiệp (FPT Software)

Dự án đồ án thực tế tốt nghiệp đề xuất ứng dụng quản lý và đặt trước tài nguyên nội bộ cho FPT Software (VKU - Trường Đại học Công nghệ Thông tin & Truyền thông Việt - Hàn).

---

## 🚀 Công Nghệ Sử Dụng (Tech Stack)
- **Ngôn ngữ:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Kiến trúc:** Clean Architecture (Data - Domain - UI Layer) + MVVM
- **Dependency Injection:** Dagger Hilt
- **Local Database & State:** Room Database, Reactive StateFlow, DataStore
- **Web Portal:** Web Admin Management Dashboard (HTML5, CSS Modern Dark Theme, Responsive)

---

## 📌 Các Tính Năng Nghiệp Vụ Chính
1. **UC04 - Đặt trước phòng họp & Bàn làm việc:** 
   - Đặt theo lịch 7 ngày, chọn khung giờ slot thời gian thực.
   - Modal Bottom Sheet xác nhận tóm tắt và nhập **Mục đích cuộc họp** (`purpose`).
   - Kiểm tra ràng buộc và ngăn chặn đặt chỗ nếu tài khoản vi phạm bị khóa.
2. **UC05 - Check-in mã QR tại vị trí:**
   - Giao diện Viewfinder giả lập Camera quét mã kèm tia laser chuyển động.
   - Nhận diện và đối soát mã QR thực tế của phòng (`QR_RES_1`, `QR_GOSLING`,...).
   - Hỗ trợ nút quét nhanh phục vụ demo, cập nhật trạng thái xanh lá **Đã Check-in** và tự động ghi sự kiện vào Audit Log.
3. **UC08 - Nhật ký hệ thống (Audit Logs):**
   - Ghi vết mọi sự kiện hệ thống (Đăng nhập nhân viên, Đặt chỗ thành công, Check-in QR, Mở khóa tài khoản) theo thời gian thực.
4. **UC12 - Quản lý vi phạm No-show & Khóa/Mở khóa tài khoản:**
   - Theo dõi số lần vi phạm No-show ($0 \rightarrow 4/3$ lần). Phân cấp: `ĐANG HOẠT ĐỘNG`, `CẢNH BÁO`, `ĐÃ KHÓA`.
   - Admin Hub trên di động và Web Admin cho phép Quản trị viên FPT mở khóa tài khoản và gửi email nhắc nhở.
5. **Giao diện Web Admin Portal:**
   - Trực quan hóa danh mục không gian, sơ đồ vị trí, quản lý tài khoản vi phạm và nhật ký vận hành.

---

## 👥 Tài Khoản Demo Phục Vụ Kiểm Thử & Chấm Điểm
Tại màn hình đăng nhập, ứng dụng trang bị sẵn 3 chip một chạm để chuyển đổi tài khoản demo:
- **Quản trị viên (Admin):** Tài khoản `admin` / Mật khẩu `admin123`
- **Nhân viên bình thường:** Tài khoản `employee` (Trần Văn Nam) / Mật khẩu `emp123`
- **Nhân viên bị khóa vi phạm No-show:** Tài khoản `ducbt` (Bùi Thế Đức - vi phạm 4/3 lần) / Mật khẩu `123456`

---

## 🛠️ Hướng Dẫn Biên Dịch & Chạy Ứng Dụng
```bash
# Clone dự án
git clone https://github.com/cystal-06/FspaceBooking.git
cd FspaceBooking

# Biên dịch gói APK Debug
./gradlew assembleDebug

# Chạy kiểm thử tự động
./gradlew test
```
File APK sau khi build nằm tại: `app/build/outputs/apk/debug/app-debug.apk`
