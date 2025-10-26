
package org.example.boxes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "AI聊天")
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * 处理AI聊天请求
     *
     * @param request 请求参数
     * @return RestResult 响应结果
     */
    @PostMapping("/chat")
    @ApiOperation("AI聊天")
    public RestResult<?> chat(@Valid @RequestBody AiChatRequestDTO request) {
        return aiChatService.handleAiChat(request);
    }
}
