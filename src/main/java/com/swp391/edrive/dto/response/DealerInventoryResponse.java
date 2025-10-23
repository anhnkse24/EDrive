package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DealerInventoryResponse {
    Long inventoryId;
    Long dealerId;
    Long modelId;
    Long versionId;
    Long versionColorId;
    String modelName;
    String versionName;
    String colorName;
    String colorCode;
    Integer onHand;
    Integer reserved;
    Integer available; // onHand - reserved (>= 0)
}
