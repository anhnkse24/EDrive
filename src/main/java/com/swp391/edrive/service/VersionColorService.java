package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.VersionColorPricePatchRequest;
import com.swp391.edrive.dto.request.VersionColorUpsertRequest;
import com.swp391.edrive.dto.response.ColorBriefResponse;

import java.util.List;

public interface VersionColorService {
    List<ColorBriefResponse> list(Long versionId, Boolean active);
    ColorBriefResponse create(Long versionId, VersionColorUpsertRequest req);
    ColorBriefResponse update(Long versionId, Long colorId, VersionColorUpsertRequest req);
    ColorBriefResponse patchPrice(Long versionId, Long colorId, VersionColorPricePatchRequest req);
    ColorBriefResponse activate(Long versionId, Long colorId, boolean active);
    void delete(Long versionId, Long colorId);
}
