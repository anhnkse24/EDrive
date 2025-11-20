package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerInventoryUpdateRequest {
    @NotNull
    @Min(0)
    private Integer onHand;

    @NotNull
    @Min(0)
    private Integer reserved;
}
