package org.example.boxes.service;

import org.example.boxes.dto.AiChatRequestDTO;
import org.example.boxes.result.RestResult;

/**
 * AI聊天服务接口
 *
 * @author 14577
 */
public interface AiChatService {

    /**
     * 处理AI聊天请求
     *
     * @param request 请求参数
     * @return RestResult 响应结果
     */
    RestResult<?> handleAiChat(AiChatRequestDTO request);
}