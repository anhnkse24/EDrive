package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateContractTermsRequest {
    @NotBlank
    public String terms;
}
