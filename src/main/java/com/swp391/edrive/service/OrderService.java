package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CreateOrderFromContractRequest;
import com.swp391.edrive.dto.request.UpdateOrderStatusRequest;
import com.swp391.edrive.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createFromContract(CreateOrderFromContractRequest req);
    OrderResponse get(Long id);
    List<OrderResponse> list();
    OrderResponse updateStatus(Long id, UpdateOrderStatusRequest req);

}
