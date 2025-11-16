package com.swp391.edrive.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorResponse {
    private Long colorId;
    private String colorName;
    private String hexCode;
    private boolean inUse;
}
