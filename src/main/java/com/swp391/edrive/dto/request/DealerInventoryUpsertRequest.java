package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerInventoryUpsertRequest {
    @NotNull
    private Long versionColorId; // inventory key theo dealer + versionColor

    @NotNull @Min(0)
    private Integer onHand;      // số xe thực tế có

    @NotNull
    @Min(0)
    private Integer reserved;    // số xe đã giữ chỗ/bán (không dùng cho test drive)
}
