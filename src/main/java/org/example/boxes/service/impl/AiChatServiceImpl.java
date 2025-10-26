package org.example.boxes.service.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.AiChatRequestDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.AiChatService;
import org.springframework.stereotype.Service;

/**
 * AI聊天服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final UserRepository userRepository;

    @Override
    public RestResult<?> handleAiChat(AiChatRequestDTO request) {
        try {
            // 校验用户是否存在
            UserDO user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                return RestResult.fail("用户不存在");
            }

            // 调用AI服务获取响应结果（模拟）
            Map<String, Object> aiResponse = callAiService(request.getMessage());

            // 记录用户消息与AI回复日志（此处省略具体实现）

            return RestResult.success(aiResponse);
        } catch (Exception e) {
            log.error("处理AI聊天请求异常", e);
            return RestResult.systemError();
        }
    }

    /**
     * 模拟调用AI服务的方法
     *
     * @param message 用户输入的消息
     * @return AI返回的结果
     */
    private Map<String, Object> callAiService(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("reply", "这是来自AI的回复：" + message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}