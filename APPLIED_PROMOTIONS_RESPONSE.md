# ✅ Response Báo giá với Danh sách Promotion đã áp dụng

## Tổng quan

Response báo giá giờ đây bao gồm thông tin chi tiết về các promotion đã được áp dụng, giúp:
- ✅ Truy vết promotion nào đã được dùng
- ✅ Hiển thị cho khách hàng biết họ được giảm giá từ đâu
- ✅ Audit và báo cáo doanh số theo promotion

## Response Format MỚI

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
  "statusCode": 200,
  "message": "Báo giá được tạo thành công",
  "data": {
    "quotationId": 100,
    "dealerId": 1,
    "dealerName": "Đại lý ABC",
    "createdByUserName": "Nguyễn Văn A",
    
    "vehicleId": 1,
    "modelName": "VinFast VF8",
    "version": "Plus",
    
    "customerId": 5,
    "customerFullName": "Trần Thị B",
    "customerEmail": "tranb@email.com",
    "customerPhone": "0901234567",
    
    "paymentMethod": "CASH",
    "quotationStatus": "PENDING",
    
    "selectedServices": [
      {
        "serviceId": 1,
        "serviceName": "Phim cách nhiệt cao cấp 3M",
        "description": "Phim cách nhiệt 3M...",
        "price": 8500000,
        "category": "Bảo vệ",
        "quantity": 1,
        "totalPrice": 8500000
      },
      {
        "serviceId": 2,
        "serviceName": "Bộ sạc Wallbox 7kW",
        "description": "Bộ sạc nhanh...",
        "price": 15000000,
        "category": "Sạc điện",
        "quantity": 1,
        "totalPrice": 15000000
      },
      {
        "serviceId": 3,
        "serviceName": "Gói bảo hành mở rộng 2 năm",
        "description": "Bảo hành mở rộng...",
        "price": 25000000,
        "category": "Bảo hành",
        "quantity": 1,
        "totalPrice": 25000000
      }
    ],
    
    "appliedPromotions": [
      {
        "promoId": 1,
        "title": "Khuyến mãi VF8 tháng 11",
        "description": "Giảm 50 triệu cho VF8 trong tháng 11/2025",
        "discountType": "FIXED_AMOUNT",
        "discountValue": 50000000,
        "discountAmount": 50000000,
        "startDate": "2025-11-01",
        "endDate": "2025-11-30",
        "applicableTo": "VEHICLE"
      },
      {
        "promoId": 2,
        "title": "Khách hàng VIP",
        "description": "Giảm 5% cho khách hàng VIP",
        "discountType": "PERCENTAGE",
        "discountValue": 5.0,
        "discountAmount": 42500000,
        "startDate": "2025-01-01",
        "endDate": "2025-12-31",
        "applicableTo": "CUSTOMER"
      }
    ],
    
    "unitPrice": 850000000,
    "promotionDiscountAmount": 92500000,
    "additionalServicesTotal": 48500000,
    "vatAmount": 80650000,
    "grandTotal": 886650000
  }
}
```

## Chi tiết AppliedPromotionResponse

### Cấu trúc:
```typescript
{
  promoId: number;              // ID của promotion
  title: string;                // Tên promotion
  description: string;          // Mô tả chi tiết
  discountType: string;         // "PERCENTAGE" hoặc "FIXED_AMOUNT"
  discountValue: number;        // Giá trị giảm (% hoặc số tiền)
  discountAmount: number;       // Số tiền giảm THỰC TẾ (đã tính)
  startDate: string;            // Ngày bắt đầu
  endDate: string;              // Ngày kết thúc
  applicableTo: string;         // "VEHICLE" hoặc "CUSTOMER"
}
```

### Ý nghĩa các fields:

- **promoId**: ID để trace lại promotion trong database
- **title**: Tên hiển thị cho khách hàng
- **description**: Mô tả chi tiết về promotion
- **discountType**: 
  - `PERCENTAGE`: Giảm theo phần trăm
  - `FIXED_AMOUNT`: Giảm số tiền cố định
- **discountValue**: 
  - Nếu PERCENTAGE: giá trị % (VD: 5.0 = 5%)
  - Nếu FIXED_AMOUNT: số tiền (VD: 50000000 = 50 triệu)
- **discountAmount**: Số tiền giảm THỰC TẾ sau khi tính toán
- **startDate/endDate**: Thời gian hiệu lực
- **applicableTo**: Promotion áp dụng cho xe hay khách hàng

## Ví dụ hiển thị trên UI

### React Component:
```jsx
function QuotationDetail({ quotation }) {
  return (
    <div className="quotation-detail">
      {/* Thông tin xe */}
      <section className="vehicle-info">
        <h3>{quotation.modelName} {quotation.version}</h3>
        <p className="price">{quotation.unitPrice.toLocaleString('vi-VN')} VNĐ</p>
      </section>

      {/* Danh sách promotion đã áp dụng */}
      {quotation.appliedPromotions && quotation.appliedPromotions.length > 0 && (
        <section className="applied-promotions">
          <h4>🎁 Khuyến mãi đã áp dụng:</h4>
          {quotation.appliedPromotions.map(promo => (
            <div key={promo.promoId} className="promotion-item">
              <div className="promo-header">
                <strong>{promo.title}</strong>
                <span className="badge">{promo.applicableTo}</span>
              </div>
              <p className="promo-description">{promo.description}</p>
              <div className="promo-discount">
                {promo.discountType === 'PERCENTAGE' ? (
                  <span>Giảm {promo.discountValue}%</span>
                ) : (
                  <span>Giảm {promo.discountValue.toLocaleString('vi-VN')} VNĐ</span>
                )}
                <strong className="discount-amount">
                  - {promo.discountAmount.toLocaleString('vi-VN')} VNĐ
                </strong>
              </div>
              <small className="promo-period">
                Hiệu lực: {promo.startDate} đến {promo.endDate}
              </small>
            </div>
          ))}
          <div className="total-discount">
            <strong>Tổng giảm giá:</strong>
            <strong className="amount">
              - {quotation.promotionDiscountAmount.toLocaleString('vi-VN')} VNĐ
            </strong>
          </div>
        </section>
      )}

      {/* Dịch vụ bổ sung */}
      {quotation.selectedServices && quotation.selectedServices.length > 0 && (
        <section className="selected-services">
          <h4>🔧 Dịch vụ bổ sung:</h4>
          {quotation.selectedServices.map(service => (
            <div key={service.serviceId} className="service-item">
              <span>{service.serviceName}</span>
              <span>{service.price.toLocaleString('vi-VN')} VNĐ</span>
            </div>
          ))}
        </section>
      )}

      {/* Tổng cộng */}
      <section className="price-summary">
        <div className="line-item">
          <span>Giá xe:</span>
          <span>{quotation.unitPrice.toLocaleString('vi-VN')} VNĐ</span>
        </div>
        {quotation.promotionDiscountAmount > 0 && (
          <div className="line-item discount">
            <span>Giảm giá khuyến mãi:</span>
            <span>- {quotation.promotionDiscountAmount.toLocaleString('vi-VN')} VNĐ</span>
          </div>
        )}
        {quotation.additionalServicesTotal > 0 && (
          <div className="line-item">
            <span>Dịch vụ bổ sung:</span>
            <span>+ {quotation.additionalServicesTotal.toLocaleString('vi-VN')} VNĐ</span>
          </div>
        )}
        <div className="line-item">
          <span>VAT (10%):</span>
          <span>+ {quotation.vatAmount.toLocaleString('vi-VN')} VNĐ</span>
        </div>
        <div className="line-item total">
          <strong>TỔNG CỘNG:</strong>
          <strong>{quotation.grandTotal.toLocaleString('vi-VN')} VNĐ</strong>
        </div>
      </section>
    </div>
  );
}
```

### CSS Example:
```css
.applied-promotions {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 10px;
  margin: 20px 0;
}

.promotion-item {
  background: rgba(255, 255, 255, 0.1);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.promo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.badge {
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.discount-amount {
  color: #FFD700;
  font-size: 18px;
}

.total-discount {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid rgba(255, 255, 255, 0.3);
  font-size: 18px;
}
```

## Breakdown tính toán

### Scenario: 2 promotions áp dụng

**Promotions:**
1. **Vehicle Promotion**: Giảm 50 triệu (FIXED_AMOUNT)
2. **Customer Promotion**: Giảm 5% (PERCENTAGE)

**Tính toán:**
```
Giá xe (unitPrice):              850,000,000 VNĐ

Promotion 1 (FIXED):            -50,000,000 VNĐ
Promotion 2 (5%):      850M × 5% = -42,500,000 VNĐ
                                  ─────────────────
Tổng giảm giá:                   -92,500,000 VNĐ

Giá sau giảm:                     757,500,000 VNĐ
Dịch vụ bổ sung:                 +48,500,000 VNĐ
                                  ─────────────────
Tổng trước VAT:                   806,000,000 VNĐ
VAT (10%):                       +80,600,000 VNĐ
                                  ═════════════════
TỔNG CỘNG:                        886,600,000 VNĐ
```

## Database Schema

### Bảng quotation_promotion (Many-to-Many)
```sql
CREATE TABLE quotation_promotion (
    quotation_id BIGINT NOT NULL,
    promotion_id BIGINT NOT NULL,
    PRIMARY KEY (quotation_id, promotion_id),
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(promo_id)
);
```

### Query để xem promotion của quotation:
```sql
SELECT 
    q.quotation_id,
    q.unit_price,
    q.promotion_discount_amount,
    p.promo_id,
    p.title,
    p.discount_type,
    p.discount_value
FROM quotations q
LEFT JOIN quotation_promotion qp ON q.quotation_id = qp.quotation_id
LEFT JOIN promotions p ON qp.promotion_id = p.promo_id
WHERE q.quotation_id = 100;
```

## In báo giá PDF

Khi in báo giá ra PDF, phần promotion nên hiển thị:

```
╔════════════════════════════════════════════════════════════╗
║                    BÁO GIÁ XE ĐIỆN                         ║
║                   Số: QT-2025-000100                       ║
╚════════════════════════════════════════════════════════════╝

Xe: VinFast VF8 Plus                        850,000,000 VNĐ

KHUYẾN MÃI ÁP DỤNG:
─────────────────────────────────────────────────────────────
1. Khuyến mãi VF8 tháng 11
   Giảm 50 triệu cho VF8 trong tháng 11/2025
   Giảm giá: -50,000,000 VNĐ
   
2. Khách hàng VIP  
   Giảm 5% cho khách hàng VIP
   Giảm giá: -42,500,000 VNĐ
─────────────────────────────────────────────────────────────
Tổng giảm giá:                              -92,500,000 VNĐ

DỊCH VỤ BỔ SUNG:
─────────────────────────────────────────────────────────────
- Phim cách nhiệt 3M                          8,500,000 VNĐ
- Bộ sạc Wallbox 7kW                         15,000,000 VNĐ
- Gói bảo hành mở rộng 2 năm                 25,000,000 VNĐ
─────────────────────────────────────────────────────────────
Tổng dịch vụ:                                48,500,000 VNĐ

TỔNG KẾT:
═════════════════════════════════════════════════════════════
Giá xe:                                     850,000,000 VNĐ
Giảm giá khuyến mãi:                        -92,500,000 VNĐ
Dịch vụ bổ sung:                            +48,500,000 VNĐ
─────────────────────────────────────────────────────────────
Tổng trước VAT:                             806,000,000 VNĐ
VAT (10%):                                  +80,600,000 VNĐ
═════════════════════════════════════════════════════════════
TỔNG CỘNG:                                  886,600,000 VNĐ
```

## Lợi ích

### 1. Minh bạch cho khách hàng
- Khách hàng thấy rõ được giảm bao nhiêu từ promotion nào
- Tăng độ tin cậy

### 2. Quản lý và báo cáo
- Biết được promotion nào được sử dụng nhiều
- Tính hiệu quả của từng promotion
- Báo cáo doanh số theo promotion

### 3. Audit trail
- Lưu lại lịch sử promotion đã áp dụng
- Không bị ảnh hưởng khi promotion thay đổi sau này

### 4. Marketing insights
- Phân tích promotion nào hiệu quả
- Tối ưu chiến lược khuyến mãi

---
**Updated**: 2025-11-19  
**Status**: ✅ Production Ready  
**Feature**: Applied Promotions Display in Quotation Response

