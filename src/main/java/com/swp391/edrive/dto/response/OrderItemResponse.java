package com.swp391.edrive.dto.response;

import java.math.BigDecimal;


public class OrderItemResponse {
    public Long vehicleId;
    public String vehicleName;      // Model name
    public String vehicleVersion;   // Version
    public String colorName;        // Color
    public String vehicleImageUrl;  // Image URL
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal itemSubtotal;
    public BigDecimal itemDiscount;
    public BigDecimal itemTotal;
}