package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户发送的消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}