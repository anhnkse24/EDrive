package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerInventoryAdjustRequest {
    // delta có thể âm hoặc dương, tổng onHand/reserved cuối cùng không được < 0
    private Integer onHandDelta;
    private Integer reservedDelta;
}
