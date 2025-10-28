package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 优雅降级请求数据传输对象
 *
 * @author 14577
 */
@Data
public class FallbackRequestDTO {

    /**
     * 服务标识
     */
    @NotBlank(message = "服务标识不能为空")
    private String serviceId;

    /**
     * 降级原因
     */
    @NotBlank(message = "降级原因不能为空")
    private String fallbackReason;
}