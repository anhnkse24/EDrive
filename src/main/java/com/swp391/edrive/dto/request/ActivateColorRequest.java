package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateColorRequest {
    @NotNull
    private Boolean active;
}
