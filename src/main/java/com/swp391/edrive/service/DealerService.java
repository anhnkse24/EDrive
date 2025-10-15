package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.DealerRequest;
import com.swp391.edrive.dto.response.DealerResponse;

import java.util.List;

public interface DealerService {
    DealerResponse createDealer(DealerRequest req);
    DealerResponse updateDealer(Long dealerId, DealerRequest req);
    void deleteDealer(Long dealerId);
    DealerResponse getDealerById(Long dealerId);
    List<DealerResponse> getAllDealers();
}
