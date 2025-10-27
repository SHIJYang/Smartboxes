package org.example.boxes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI聊天请求参数DTO
 *
 * @author 14577
 */
@Data
public class AiChatRequestDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户发送的消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容长度不能超过1000个字符")
    private String message;
}