package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ColorRequest;
import com.swp391.edrive.dto.response.ColorResponse;

import java.util.List;

public interface ColorService {
    ColorResponse create(ColorRequest req);
    ColorResponse update(Long id, ColorRequest req);
    void delete(Long id, boolean force);

    ColorResponse getById(Long id);
    List<ColorResponse> getAll();
    List<ColorResponse> search(String q);// search theo tên màu
}
