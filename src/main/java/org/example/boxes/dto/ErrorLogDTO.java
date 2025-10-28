package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 错误日志数据传输对象
 *
 * @author 14577
 */
@Data
public class ErrorLogDTO {

    /**
     * 错误编码
     */
    @NotBlank(message = "错误编码不能为空")
    private String errorCode;

    /**
     * 错误信息
     */
    @NotBlank(message = "错误信息不能为空")
    private String errorMessage;

    /**
     * 错误时间
     */
    @NotBlank(message = "错误时间不能为空")
    private LocalDateTime errorTime;
}