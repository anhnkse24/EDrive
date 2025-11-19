# ✅ Tính toán Promotion Discount trong Báo giá

## Tổng quan

Hệ thống giờ đây tự động tính toán giảm giá từ các promotion phù hợp khi tạo báo giá.

## Cách hoạt động

### 1. Khi tạo báo giá, hệ thống sẽ:

```
1. Lấy danh sách promotion của Dealer
   ↓
2. Lọc các promotion còn hiệu lực (startDate <= today <= endDate)
   ↓
3. Kiểm tra promotion có áp dụng cho Vehicle hoặc Customer không
   ↓
4. Tính giảm giá (PERCENTAGE hoặc FIXED_AMOUNT)
   ↓
5. Cộng dồn tất cả giảm giá
   ↓
6. Đảm bảo tổng giảm giá không vượt quá giá xe
```

### 2. Loại Promotion

#### A. VEHICLE Promotion
- **Áp dụng**: Cho các xe cụ thể
- **Điều kiện**: Vehicle phải có trong `promotion.vehicles`
- **Ví dụ**: "Giảm 50 triệu cho VinFast VF8 Plus"

#### B. CUSTOMER Promotion
- **Áp dụng**: Cho khách hàng cụ thể
- **Điều kiện**: Customer phải có trong `promotion.customers`
- **Ví dụ**: "Giảm 10% cho khách hàng VIP"

### 3. Loại giảm giá

#### A. PERCENTAGE (Phần trăm)
```java
discount = unitPrice × (discountValue / 100)
```
**Ví dụ**: Xe 850 triệu, giảm 5%
```
discount = 850,000,000 × (5 / 100) = 42,500,000 VNĐ
```

#### B. FIXED_AMOUNT (Số tiền cố định)
```java
discount = discountValue
```
**Ví dụ**: Giảm cố định 50 triệu
```
discount = 50,000,000 VNĐ
```

## Ví dụ thực tế

### Scenario 1: Promotion cho Vehicle

**Tạo promotion:**
```sql
INSERT INTO promotions (title, description, discount_type, discount_value, start_date, end_date, applicable_to, dealer_id)
VALUES ('Giảm giá VF8', 'Giảm 50 triệu cho VF8', 'FIXED_AMOUNT', 50000000, '2025-11-01', '2025-12-31', 'VEHICLE', 1);

-- Gán xe cho promotion
INSERT INTO vehicle_promotion (promotion_id, vehicle_id) VALUES (1, 1);
```

**Kết quả khi tạo báo giá:**
```json
{
  "vehicleId": 1,
  "customerId": 5,
  "paymentMethod": "CASH",
  "selectedServiceIds": [1, 2]
}

Response:
{
  "unitPrice": 850000000,
  "promotionDiscountAmount": 50000000,  // ← ĐÃ TÍNH!
  "additionalServicesTotal": 23500000,
  "vatAmount": 82350000,
  "grandTotal": 905850000
}
```

### Scenario 2: Promotion cho Customer

**Tạo promotion:**
```sql
INSERT INTO promotions (title, description, discount_type, discount_value, start_date, end_date, applicable_to, dealer_id)
VALUES ('Khách hàng VIP', 'Giảm 10% cho KH VIP', 'PERCENTAGE', 10, '2025-01-01', '2025-12-31', 'CUSTOMER', 1);

-- Gán customer cho promotion
INSERT INTO customer_promotion (promotion_id, customer_id) VALUES (2, 5);
```

**Kết quả:**
```
unitPrice: 850,000,000 VNĐ
discount (10%): 85,000,000 VNĐ
priceAfterDiscount: 765,000,000 VNĐ
```

### Scenario 3: Nhiều promotion cùng lúc

**Promotions:**
1. Vehicle promotion: Giảm 50 triệu (FIXED_AMOUNT)
2. Customer promotion: Giảm 5% (PERCENTAGE)

**Tính toán:**
```
unitPrice: 850,000,000 VNĐ

Promotion 1 (FIXED_AMOUNT): 50,000,000 VNĐ
Promotion 2 (PERCENTAGE 5%): 850,000,000 × 5% = 42,500,000 VNĐ

totalDiscount: 50,000,000 + 42,500,000 = 92,500,000 VNĐ

priceAfterDiscount: 850,000,000 - 92,500,000 = 757,500,000 VNĐ
```

## Validation Rules

### 1. Promotion phải còn hiệu lực
```java
if (startDate != null && today < startDate) → SKIP
if (endDate != null && today > endDate) → SKIP
```

### 2. Promotion phải áp dụng đúng đối tượng
```java
// VEHICLE promotion
if (promotion.vehicles NOT contains vehicle) → SKIP

// CUSTOMER promotion  
if (promotion.customers NOT contains customer) → SKIP
```

### 3. Giảm giá không vượt quá giá xe
```java
if (totalDiscount > unitPrice) {
    totalDiscount = unitPrice
}
```

## Response Example

### Request:
```json
{
  "vehicleId": 1,
  "customerId": 5,
  "paymentMethod": "CASH",
  "selectedServiceIds": [1, 2, 3]
}
```

### Response (có promotion):
```json
{
  "quotationId": 100,
  "vehicleId": 1,
  "customerId": 5,
  
  "unitPrice": 850000000,
  "promotionDiscountAmount": 92500000,  // ← Tổng giảm giá từ promotions
  "additionalServicesTotal": 48500000,
  "vatAmount": 80650000,
  "grandTotal": 886650000,
  
  "selectedServices": [...]
}
```

### Breakdown giá:
```
Giá xe (unitPrice):              850,000,000 VNĐ
Giảm giá (promotionDiscount):   - 92,500,000 VNĐ
                                 ──────────────────
Giá xe sau giảm:                 757,500,000 VNĐ
Dịch vụ bổ sung:                + 48,500,000 VNĐ
                                 ──────────────────
Tổng trước VAT:                  806,000,000 VNĐ
VAT 10%:                        + 80,600,000 VNĐ
                                 ══════════════════
TỔNG CỘNG:                       886,600,000 VNĐ
```

## Testing

### Bước 1: Tạo Dealer
```sql
INSERT INTO dealers (dealer_name, ...) VALUES ('Đại lý ABC', ...);
-- dealer_id = 1
```

### Bước 2: Tạo Vehicle
```sql
INSERT INTO vehicles (model_name, price_retail, ...) VALUES ('VF8 Plus', 850000000, ...);
-- vehicle_id = 1
```

### Bước 3: Tạo Promotion
```sql
-- Promotion giảm 50 triệu cho VF8
INSERT INTO promotions (
    title, description, discount_type, discount_value, 
    start_date, end_date, applicable_to, dealer_id
) VALUES (
    'Khuyến mãi VF8', 
    'Giảm 50 triệu cho VF8', 
    'FIXED_AMOUNT', 
    50000000, 
    '2025-11-01', 
    '2025-12-31', 
    'VEHICLE', 
    1
);
-- promo_id = 1
```

### Bước 4: Gán Vehicle cho Promotion
```sql
INSERT INTO vehicle_promotion (promotion_id, vehicle_id) 
VALUES (1, 1);
```

### Bước 5: Tạo báo giá
```bash
POST /api/quotations/create
{
  "vehicleId": 1,
  "customerId": 5,
  "paymentMethod": "CASH",
  "selectedServiceIds": []
}

# Expected:
# promotionDiscountAmount: 50000000
```

## Lưu ý quan trọng

### 1. Promotion của Dealer
- Chỉ tính các promotion thuộc về dealer của user đang tạo báo giá
- Không tính promotion của dealer khác

### 2. Nhiều promotion cùng lúc
- Hệ thống **CỘNG DỒN** tất cả promotion phù hợp
- Không giới hạn số lượng promotion áp dụng
- **Lưu ý**: Có thể cần thêm logic để giới hạn nếu cần

### 3. Promotion hết hạn
- Tự động bỏ qua nếu `today > endDate`
- Tự động bỏ qua nếu `today < startDate`

### 4. Giá được lưu
- `promotionDiscountAmount` được lưu vào database
- Nếu sau này promotion thay đổi, báo giá cũ không bị ảnh hưởng

## Troubleshooting

### Vấn đề: promotionDiscountAmount = 0

**Nguyên nhân có thể:**
1. ❌ Không có promotion nào của dealer
2. ❌ Promotion đã hết hạn (endDate < today)
3. ❌ Promotion chưa bắt đầu (startDate > today)
4. ❌ Vehicle không có trong `promotion.vehicles` (nếu VEHICLE promotion)
5. ❌ Customer không có trong `promotion.customers` (nếu CUSTOMER promotion)

**Cách kiểm tra:**
```sql
-- Kiểm tra promotion của dealer
SELECT * FROM promotions WHERE dealer_id = 1;

-- Kiểm tra vehicle có trong promotion không
SELECT * FROM vehicle_promotion WHERE vehicle_id = 1;

-- Kiểm tra customer có trong promotion không
SELECT * FROM customer_promotion WHERE customer_id = 5;

-- Kiểm tra promotion còn hiệu lực không
SELECT * FROM promotions 
WHERE dealer_id = 1 
  AND start_date <= CURRENT_DATE 
  AND end_date >= CURRENT_DATE;
```

---
**Updated**: 2025-11-19
**Status**: ✅ Production Ready
**Feature**: Automatic Promotion Discount Calculation

