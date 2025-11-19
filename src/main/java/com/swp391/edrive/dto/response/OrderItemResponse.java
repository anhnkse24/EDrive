package com.swp391.edrive.dto.response;

import java.math.BigDecimal;


public class OrderItemResponse {
    public Long vehicleId;
    public String vehicleName;
    public String vehicleVersion;
    public String colorName;
    public String vehicleImageUrl;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal itemSubtotal;
    public BigDecimal itemDiscount;
    public BigDecimal itemTotal;
}