# API Gửi Email Báo Giá Cho Khách Hàng

## Mô tả
API này cho phép gửi email kèm file PDF báo giá đến khách hàng. 
**Lưu ý quan trọng**: Chỉ có thể gửi email cho những báo giá đã được đại lý duyệt (trạng thái `ACCEPTED`).

## Endpoint

```
POST /api/quotations/{quotationId}/send-email
```

## Parameters

| Tên | Kiểu | Bắt buộc | Mô tả |
|-----|------|----------|-------|
| quotationId | Long | Có | ID của báo giá cần gửi email |

## Request Example

```http
POST /api/quotations/1/send-email
Authorization: Bearer <your-token>
```

## Response Success (200 OK)

```json
{
  "statusCode": 200,
  "message": "Gửi email báo giá thành công",
  "data": "Email đã được gửi đến khách hàng kèm file PDF báo giá"
}
```

## Response Error

### 1. Báo giá chưa được duyệt (400 Bad Request)
```json
{
  "statusCode": 400,
  "message": "Chỉ có thể gửi email cho báo giá đã được đại lý duyệt (trạng thái ACCEPTED)",
  "data": null
}
```

### 2. Báo giá không tồn tại (500 Internal Server Error)
```json
{
  "statusCode": 500,
  "message": "Lỗi khi gửi email: Không tìm thấy báo giá với ID: 999",
  "data": null
}
```

### 3. Khách hàng không có email (500 Internal Server Error)
```json
{
  "statusCode": 500,
  "message": "Lỗi khi gửi email: Khách hàng không có email để gửi báo giá",
  "data": null
}
```

## Quy trình sử dụng

1. **Tạo báo giá** bằng API `POST /api/quotations/create`
2. **Đại lý duyệt báo giá** bằng API `PUT /api/quotations/update-status` với status = "ACCEPTED"
3. **Gửi email cho khách hàng** bằng API này `POST /api/quotations/{quotationId}/send-email`

## Nội dung Email

Email được gửi đi sẽ bao gồm:

### Subject (Tiêu đề)
```
Báo giá xe điện từ [Tên đại lý]
```

### Body (Nội dung)
- Lời chào khách hàng
- Thông tin xe: Model, phiên bản
- Thông tin báo giá:
  - Mã báo giá
  - Giá xe
  - Giảm giá khuyến mãi (nếu có)
  - Tổng giá trị (đã bao gồm VAT)
- Thông tin liên hệ đại lý:
  - Tên đại lý
  - Địa chỉ
  - Điện thoại
  - Email

### Attachment (File đính kèm)
File PDF báo giá chi tiết với tên: `Bao-gia-{quotationId}.pdf`

## Ví dụ nội dung Email

```
Kính gửi Quý khách Nguyễn Văn Minh,

Cảm ơn Quý khách đã quan tâm đến sản phẩm xe điện của chúng tôi.

Chúng tôi xin gửi đến Quý khách báo giá chi tiết cho xe VF 8 phiên bản Standard.

Thông tin báo giá:
- Mã báo giá: #1
- Xe: VF 8 Standard
- Giá xe: 2,000,000,000 VNĐ
- Giảm giá khuyến mãi: 100,000,000 VNĐ
- Tổng giá trị (đã bao gồm VAT): 2,099,350,000 VNĐ

Vui lòng xem chi tiết trong file PDF đính kèm.

Để biết thêm thông tin hoặc đặt lịch lái thử, vui lòng liên hệ:
Đại lý: E-Drive Hà Nội
Địa chỉ: 123 Láng Hạ, Ba Đình, Hà Nội
Điện thoại: 0901234567
Email: hanoi@edrive.vn

Trân trọng,
E-Drive Hà Nội
```

## Cấu hình Email

Đảm bảo rằng file `application.properties` đã được cấu hình đúng:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Lưu ý kỹ thuật

1. File PDF tạm thời sẽ được tạo và tự động xóa sau khi gửi email thành công
2. API sử dụng `EmailService.sendEmailWithAttachment()` để gửi email kèm file
3. Chỉ báo giá với trạng thái `ACCEPTED` mới có thể gửi email
4. Khách hàng phải có email hợp lệ trong hệ thống

## Testing

### Postman/Swagger Test
1. Login và lấy JWT token
2. Tạo báo giá mới
3. Cập nhật trạng thái báo giá thành ACCEPTED
4. Gọi API send-email với quotationId

### Kiểm tra Email
- Kiểm tra hộp thư đến của khách hàng
- Kiểm tra file PDF đính kèm có mở được không
- Kiểm tra nội dung email có đầy đủ thông tin không

