package com.carddemo.ops.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Map;

@Data
public class JobTriggerRequest {
    @NotBlank
    private String runMode;
    private Map<String, String> parameters;
}
