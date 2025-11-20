# Test Case: Gửi Email Báo Giá

## Prerequisites
- Server đang chạy
- Đã cấu hình email trong application.properties
- Đã có JWT token để authenticate

## Test Scenario 1: Gửi email thành công

### Step 1: Tạo báo giá mới
```bash
POST http://localhost:8080/api/quotations/create
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "vehicleId": 1,
  "customerId": 1,
  "paymentMethod": "TRẢ_THẲNG",
  "selectedServiceIds": [1, 2, 3],
  "selectedPromotionIds": [1]
}
```

**Expected Response**: Status 200, quotationId = X (ví dụ: 1)

### Step 2: Duyệt báo giá
```bash
PUT http://localhost:8080/api/quotations/update-status
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "quotationId": 1,
  "status": "ACCEPTED"
}
```

**Expected Response**: Status 200, quotationStatus = "ACCEPTED"

### Step 3: Gửi email báo giá
```bash
POST http://localhost:8080/api/quotations/1/send-email
Authorization: Bearer <your-token>
```

**Expected Response**:
```json
{
  "statusCode": 200,
  "message": "Gửi email báo giá thành công",
  "data": "Email đã được gửi đến khách hàng kèm file PDF báo giá"
}
```

**Kiểm tra**:
- ✅ Email được gửi đến địa chỉ email của khách hàng
- ✅ File PDF đính kèm có thể mở được
- ✅ Nội dung email đầy đủ thông tin
- ✅ PDF hiển thị đúng thông tin báo giá

---

## Test Scenario 2: Gửi email cho báo giá chưa được duyệt (Expected Fail)

### Step 1: Tạo báo giá mới
```bash
POST http://localhost:8080/api/quotations/create
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "vehicleId": 1,
  "customerId": 1,
  "selectedServiceIds": [1],
  "selectedPromotionIds": []
}
```

**Expected Response**: Status 200, quotationId = Y (ví dụ: 2)

### Step 2: Thử gửi email ngay (KHÔNG duyệt trước)
```bash
POST http://localhost:8080/api/quotations/2/send-email
Authorization: Bearer <your-token>
```

**Expected Response**:
```json
{
  "statusCode": 400,
  "message": "Chỉ có thể gửi email cho báo giá đã được đại lý duyệt (trạng thái ACCEPTED)",
  "data": null
}
```

**Kiểm tra**:
- ✅ Trả về lỗi 400
- ✅ Message thông báo rõ ràng
- ✅ Không gửi email

---

## Test Scenario 3: Gửi email cho báo giá không tồn tại (Expected Fail)

```bash
POST http://localhost:8080/api/quotations/99999/send-email
Authorization: Bearer <your-token>
```

**Expected Response**:
```json
{
  "statusCode": 500,
  "message": "Lỗi khi gửi email: Không tìm thấy báo giá với ID: 99999",
  "data": null
}
```

**Kiểm tra**:
- ✅ Trả về lỗi 500
- ✅ Message thông báo rõ ràng
- ✅ Không gửi email

---

## Test Scenario 4: Khách hàng không có email (Expected Fail)

### Prerequisites
- Tạo customer không có email hoặc email null

### Steps
1. Tạo báo giá với customer không có email
2. Duyệt báo giá (ACCEPTED)
3. Gửi email

**Expected Response**:
```json
{
  "statusCode": 500,
  "message": "Lỗi khi gửi email: Khách hàng không có email để gửi báo giá",
  "data": null
}
```

---

## Checklist Kiểm tra Email

### Subject
- [ ] Có chứa tên đại lý
- [ ] Format: "Báo giá xe điện từ [Tên đại lý]"

### Body
- [ ] Lời chào có tên khách hàng
- [ ] Thông tin xe đầy đủ (model + version)
- [ ] Mã báo giá hiển thị đúng
- [ ] Giá xe format đúng (có dấu phẩy)
- [ ] Giảm giá khuyến mãi (nếu có)
- [ ] Tổng giá trị đã bao gồm VAT
- [ ] Thông tin đại lý đầy đủ:
  - [ ] Tên đại lý
  - [ ] Địa chỉ
  - [ ] Điện thoại
  - [ ] Email

### Attachment
- [ ] File PDF có tên: Bao-gia-{quotationId}.pdf
- [ ] File PDF mở được không lỗi
- [ ] PDF hiển thị tiếng Việt đúng (không bị mất dấu)
- [ ] PDF có đầy đủ thông tin:
  - [ ] Logo/Header
  - [ ] Thông tin xe
  - [ ] Thông tin khách hàng
  - [ ] Dịch vụ bổ sung
  - [ ] Khuyến mãi
  - [ ] Bảng giá chi tiết
  - [ ] VAT
  - [ ] Tổng giá

---

## Notes
- Email sử dụng SMTP configuration từ application.properties
- File PDF tạm sẽ tự động xóa sau khi gửi
- API chỉ hoạt động với báo giá ACCEPTED
- Cần đảm bảo customer có email hợp lệ

