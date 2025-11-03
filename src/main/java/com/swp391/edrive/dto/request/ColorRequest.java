package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorRequest {
    @NotBlank
    @Size(max = 50)
    private String colorName;

    // optional, nếu có thì phải hợp lệ dạng #RRGGBB
    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "Hex code phải dạng #RRGGBB")
    private String hexCode;
}
