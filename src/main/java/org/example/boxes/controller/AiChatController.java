package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.AiChatRequestDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.AiChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI聊天控制器
 *
 * @author 14577
 */
@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
@Tag(name = "AI聊天", description = "AI聊天相关接口")
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * 处理AI聊天请求
     *
     * @param request 请求参数
     * @return RestResult 响应结果
     */
    @PostMapping("/chat")
    @Operation(summary = "AI聊天", description = "处理用户的AI聊天请求")
    public RestResult<?> chat(@Valid @RequestBody AiChatRequestDTO request) {
        return aiChatService.handleAiChat(request);
    }
}