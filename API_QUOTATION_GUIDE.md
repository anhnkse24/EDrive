# API Tạo Báo Giá - Hướng Dẫn

## Endpoint
```
POST /api/quotations/create
```

## Request Body
```json
{
  "vehicleId": 1,
  "customerId": 5,
  "paymentMethod": "FULL_PAYMENT",
  "additionalServices": {
    "hasTintFilm": true,
    "hasWallboxCharger": true,
    "hasWarrantyExtension": false,
    "hasPPF": true,
    "hasCeramicCoating": false,
    "has360Camera": true
  }
}
```

### Mô tả các trường:
- **vehicleId**: ID của chiếc xe cần báo giá
- **customerId**: ID của khách hàng
- **paymentMethod**: Phương thức thanh toán (FULL_PAYMENT hoặc INSTALLMENT)
- **additionalServices**: Dịch vụ bổ sung (có thể bỏ qua nếu không cần)
  - **hasTintFilm**: Phim cách nhiệt cao cấp (8,500,000 VNĐ)
  - **hasWallboxCharger**: Bộ sạc Wallbox 7kW (15,000,000 VNĐ)
  - **hasWarrantyExtension**: Gói bảo hành mở rộng 2 năm (25,000,000 VNĐ)
  - **hasPPF**: PPF toàn xe (45,000,000 VNĐ)
  - **hasCeramicCoating**: Phủ Ceramic 9H (12,000,000 VNĐ)
  - **has360Camera**: Camera hành trình 360 (18,000,000 VNĐ)

## Response
```json
{
  "statusCode": 200,
  "message": "Báo giá được tạo thành công",
  "data": {
    "quotationId": 1,
    
    // THÔNG TIN XE ĐẦY ĐỦ
    "vehicleId": 1,
    "modelName": "VF 8",
    "version": "Eco",
    "batteryCapacityKwh": 87,
    "rangeKm": 420,
    "maxSpeedKmh": 180,
    "chargingTimeHours": 7.5,
    "seatingCapacity": 5,
    "motorPowerKw": 150,
    "weightKg": 2000,
    "lengthMm": 4750,
    "widthMm": 1934,
    "heightMm": 1667,
    "imageUrl": "https://example.com/vf8.png",
    "manufactureYear": 2025,
    "vehicleStatus": "AVAILABLE",
    
    // THÔNG TIN KHÁCH HÀNG ĐẦY ĐỦ
    "customerId": 5,
    "customerFullName": "Nguyễn Văn A",
    "customerDob": "1990-01-15",
    "customerGender": "Nam",
    "customerEmail": "nguyenvana@gmail.com",
    "customerPhone": "0901234567",
    "customerAddress": "123 Đường ABC, Quận 1, TP.HCM",
    "customerIdCardNo": "079090001234",
    
    // THÔNG TIN THANH TOÁN
    "paymentMethod": "FULL_PAYMENT",
    
    // DỊCH VỤ BỔ SUNG
    "additionalServices": {
      "hasTintFilm": true,
      "tintFilmPrice": 8500000,
      "hasWallboxCharger": true,
      "wallboxChargerPrice": 15000000,
      "hasWarrantyExtension": false,
      "warrantyExtensionPrice": 0,
      "hasPPF": true,
      "ppfPrice": 45000000,
      "hasCeramicCoating": false,
      "ceramicCoatingPrice": 0,
      "has360Camera": true,
      "camera360Price": 18000000,
      "totalServicesPrice": 86500000
    },
    
    // CHI TIẾT GIÁ
    "unitPrice": 1000000000,           // Giá gốc của xe
    "promotionDiscountAmount": 0,       // Giá giảm từ khuyến mãi
    "additionalServicesTotal": 86500000, // Tổng giá dịch vụ bổ sung
    "vatAmount": 108650000,             // Phí VAT 10%
    "grandTotal": 1195150000            // Tổng giá cuối cùng
  }
}
```

## Bảng Giá Dịch Vụ Bổ Sung

| Dịch Vụ | Mô Tả | Giá (VNĐ) |
|---------|-------|-----------|
| Phim cách nhiệt cao cấp | Bảo vệ xe khỏi tia UV | 8,500,000 |
| Bộ sạc Wallbox 7kW | Sạc tại nhà nhanh chóng | 15,000,000 |
| Bảo hành mở rộng 2 năm | Bảo hành thêm 2 năm | 25,000,000 |
| PPF toàn xe | Bảo vệ sơn xe | 45,000,000 |
| Phủ Ceramic 9H | Lớp phủ bảo vệ cao cấp | 12,000,000 |
| Camera hành trình 360 | Ghi hình toàn cảnh | 18,000,000 |

## Công Thức Tính Giá

1. **Giá gốc (unitPrice)**: Lấy từ `priceRetail` của xe
2. **Giá giảm khuyến mãi (promotionDiscountAmount)**: Hiện tại = 0 (có thể tích hợp sau)
3. **Giá sau giảm giá**: `unitPrice - promotionDiscountAmount`
4. **Tổng dịch vụ bổ sung**: Tổng giá các dịch vụ khách hàng chọn
5. **Tổng trước VAT**: `Giá sau giảm giá + Tổng dịch vụ bổ sung`
6. **Phí VAT (10%)**: `Tổng trước VAT × 0.10`
7. **Tổng giá cuối cùng (grandTotal)**: 
   ```
   grandTotal = Tổng trước VAT + VAT
   grandTotal = (Giá gốc - Giảm giá + Dịch vụ bổ sung) + VAT
   ```

## Ví dụ tính toán:
```
Giá gốc xe:             1,000,000,000 VNĐ
Giảm giá khuyến mãi:            0 VNĐ
--------------------------------
Giá xe sau giảm:        1,000,000,000 VNĐ

DỊCH VỤ BỔ SUNG:
- Phim cách nhiệt:          8,500,000 VNĐ
- Bộ sạc Wallbox:          15,000,000 VNĐ
- PPF toàn xe:             45,000,000 VNĐ
- Camera 360:              18,000,000 VNĐ
--------------------------------
Tổng dịch vụ:              86,500,000 VNĐ

TỔNG TRƯỚC VAT:         1,086,500,000 VNĐ
VAT (10%):                108,650,000 VNĐ
================================
TỔNG CỘNG:              1,195,150,000 VNĐ
```

## Lưu ý:
- API yêu cầu authentication token
- Xe và khách hàng phải tồn tại trong hệ thống
- VAT luôn được tính 10% trên tổng giá (xe + dịch vụ) sau khi trừ khuyến mãi
- Dịch vụ bổ sung là tùy chọn, có thể không truyền hoặc truyền `null`
- Nếu không chọn dịch vụ nào, có thể bỏ qua field `additionalServices` trong request
- Giá dịch vụ là cố định và được tự động tính toán

