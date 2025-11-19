# Migration Guide - AdditionalServices Entity

## Tổng quan thay đổi

Entity `AdditionalServices` đã được chuyển từ `@Embeddable` (nhúng vào Quotation/Order) sang **Entity độc lập** với khả năng CRUD động.

## Các thay đổi chính

### 1. Entity AdditionalServices (mới)
- **Đường dẫn**: `com.swp391.edrive.entity.AdditionalServices`
- **Bảng database**: `additional_services`
- **Chức năng**: Lưu trữ catalog các dịch vụ bổ sung có thể thêm/sửa/xóa động
- **Các trường**:
  - `serviceId` (Long, PK): ID dịch vụ
  - `serviceName` (String): Tên dịch vụ
  - `description` (Text): Mô tả chi tiết
  - `price` (BigDecimal): Giá dịch vụ
  - `isActive` (Boolean): Trạng thái hoạt động
  - `category` (String): Danh mục dịch vụ
  - `createdAt`, `updatedAt` (LocalDateTime): Timestamp

### 2. Entity QuotationService (mới)
- **Đường dẫn**: `com.swp391.edrive.entity.QuotationService`
- **Bảng database**: `quotation_services`
- **Chức năng**: Liên kết giữa Quotation và AdditionalServices (bảng trung gian)
- **Các trường**:
  - `id` (Long, PK)
  - `quotation` (ManyToOne): Báo giá liên quan
  - `service` (ManyToOne): Dịch vụ được chọn
  - `priceAtSelection` (BigDecimal): Giá tại thời điểm chọn
  - `quantity` (Integer): Số lượng
  - `note` (String): Ghi chú

### 3. Repository
- `AdditionalServicesRepository`: CRUD cho catalog dịch vụ
- `QuotationServiceRepository`: Quản lý dịch vụ đã chọn trong quotation

### 4. Service & Controller
- `AdditionalServicesService` + `AdditionalServicesServiceImpl`: Business logic
- `AdditionalServicesController`: REST API endpoints

### 5. API Endpoints

#### Quản lý Catalog Dịch vụ (AdditionalServices)
```
GET    /api/additional-services/active              - Lấy tất cả dịch vụ đang hoạt động
GET    /api/additional-services/{serviceId}         - Lấy dịch vụ theo ID
GET    /api/additional-services/category/{category} - Lấy dịch vụ theo danh mục
GET    /api/additional-services/search              - Tìm kiếm dịch vụ (có phân trang)
GET    /api/additional-services/all                 - Lấy tất cả (bao gồm inactive)
POST   /api/additional-services                     - Tạo dịch vụ mới
PUT    /api/additional-services/{serviceId}         - Cập nhật dịch vụ
PATCH  /api/additional-services/{serviceId}/deactivate - Vô hiệu hóa
PATCH  /api/additional-services/{serviceId}/activate   - Kích hoạt lại
DELETE /api/additional-services/{serviceId}         - Xóa vĩnh viễn
```

## Cần cập nhật

### ⚠️ QuotationServiceImpl
File `QuotationServiceImpl.java` **CẦN ĐƯỢC CÂP NHẬT** để sử dụng logic mới:

**Vấn đề hiện tại**:
- Đang sử dụng giá cố định (hardcode) cho các dịch vụ
- Sử dụng `AdditionalServices` như `@Embeddable` (đã lỗi thời)
- Phương thức `buildAdditionalServices()` và `calculateAdditionalServicesTotal()` cần được refactor

**Giải pháp**:
1. Inject `AdditionalServicesRepository` và `QuotationServiceRepository`
2. Khi tạo quotation, thay vì hardcode:
   - Lấy danh sách serviceId từ request
   - Query `AdditionalServices` từ database
   - Tạo các `QuotationService` entity để link với quotation
   - Tính tổng từ `priceAtSelection`

**Ví dụ cập nhật**:
```java
// Thay vì
AdditionalServices additionalServices = buildAdditionalServices(request);

// Sử dụng
List<QuotationService> quotationServices = new ArrayList<>();
for (Long serviceId : request.getSelectedServiceIds()) {
    AdditionalServices service = additionalServicesRepository.findById(serviceId)
        .orElseThrow(() -> new RuntimeException("Service not found"));
    
    QuotationService qs = new QuotationService();
    qs.setQuotation(quotation);
    qs.setService(service);
    qs.setPriceAtSelection(service.getPrice());
    qs.setQuantity(1);
    quotationServices.add(qs);
}
quotation.setQuotationServices(quotationServices);
```

### ⚠️ AdditionalServicesRequest (DTO)
File request DTO đã được cập nhật từ:
```java
// Cũ (hardcode)
private Boolean hasTintFilm;
private Boolean hasWallboxCharger;
...

// Mới (dynamic)
private String serviceName;
private String description;
private BigDecimal price;
private Boolean isActive;
private String category;
```

### ⚠️ QuotationRequest
Cần thêm field mới để nhận danh sách serviceId khi tạo quotation:
```java
private List<Long> selectedServiceIds;  // Danh sách ID dịch vụ được chọn
```

## Các bước Migration Database

### 1. Tạo bảng `additional_services`
```sql
CREATE TABLE additional_services (
    service_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(14, 2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 2. Tạo bảng `quotation_services`
```sql
CREATE TABLE quotation_services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quotation_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    price_at_selection DECIMAL(14, 2) NOT NULL,
    quantity INT DEFAULT 1,
    note TEXT,
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    FOREIGN KEY (service_id) REFERENCES additional_services(service_id)
);
```

### 3. Seed data mẫu
```sql
INSERT INTO additional_services (service_name, description, price, is_active, category) VALUES
('Phim cách nhiệt cao cấp', 'Phim cách nhiệt 3M chính hãng', 8500000, TRUE, 'Bảo vệ'),
('Bộ sạc Wallbox 7kW', 'Bộ sạc nhanh tại nhà', 15000000, TRUE, 'Sạc điện'),
('Gói bảo hành mở rộng 2 năm', 'Bảo hành mở rộng chính hãng', 25000000, TRUE, 'Bảo hành'),
('PPF toàn xe', 'Phim bảo vệ sơn toàn xe', 45000000, TRUE, 'Bảo vệ'),
('Phủ Ceramic 9H', 'Phủ nano ceramic 9H cao cấp', 12000000, TRUE, 'Bảo vệ'),
('Camera hành trình 360', 'Camera 360 độ chính hãng', 18000000, TRUE, 'Camera');
```

### 4. Xóa các cột cũ trong bảng `quotations` (nếu có)
```sql
-- Chỉ làm sau khi đã migrate data
ALTER TABLE quotations 
    DROP COLUMN has_tint_film,
    DROP COLUMN tint_film_price,
    DROP COLUMN has_wallbox_charger,
    DROP COLUMN wallbox_charger_price,
    -- ... các cột khác
```

## Testing

### 1. Test API tạo dịch vụ
```bash
POST /api/additional-services
{
  "serviceName": "Dịch vụ test",
  "description": "Mô tả test",
  "price": 5000000,
  "isActive": true,
  "category": "Test"
}
```

### 2. Test lấy danh sách dịch vụ
```bash
GET /api/additional-services/active
```

### 3. Test cập nhật dịch vụ
```bash
PUT /api/additional-services/1
{
  "price": 6000000
}
```

## Lợi ích của cách tiếp cận mới

✅ **Linh hoạt**: Thêm/sửa/xóa dịch vụ mà không cần sửa code  
✅ **Scalable**: Dễ dàng mở rộng số lượng dịch vụ  
✅ **Maintainable**: Quản lý giá tập trung, không hardcode  
✅ **History**: Lưu giá tại thời điểm chọn, không bị ảnh hưởng khi thay đổi giá sau  
✅ **Category**: Phân loại dịch vụ theo nhóm  
✅ **Reusable**: Có thể dùng cho cả Order và Quotation  

## Notes
- Đảm bảo update `QuotationServiceImpl` trước khi deploy production
- Chạy migration database trước khi start application
- Có thể tạo thêm `OrderService` entity tương tự cho Order nếu cần

