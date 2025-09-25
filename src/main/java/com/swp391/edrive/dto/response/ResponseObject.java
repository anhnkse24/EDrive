package com.swp391.edrive.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject {
    @JsonProperty("statusCode")
    private int statusCode;   // HTTP status code (200, 400, 403...)

    @JsonProperty("message")
    private String message;   // Thông báo mô tả ngắn gọn

    @JsonProperty("data")
    private Object data;      // Dữ liệu trả về (DTO, List, Map...)
}
