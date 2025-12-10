package org.example.boxes.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.AiChatRequestDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.AiChatService;
import org.example.boxes.service.SecurityService;
import org.example.boxes.service.SqlExecutionService;
import org.example.boxes.service.SqlGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AI聊天服务实现类
 * 整合自然语言处理和数据库操作功能
 *
 * @author 14577
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final UserRepository userRepository;
    private final SqlGenerationService sqlGenerationService;
    private final SqlExecutionService sqlExecutionService;
    private final SecurityService securityService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    // Ollama API 配置
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;
    
    // 构造函数注入
    public AiChatServiceImpl(UserRepository userRepository, 
                           SqlGenerationService sqlGenerationService, 
                           SqlExecutionService sqlExecutionService, 
                           SecurityService securityService) {
        this.userRepository = userRepository;
        this.sqlGenerationService = sqlGenerationService;
        this.sqlExecutionService = sqlExecutionService;
        this.securityService = securityService;
        this.webClient = WebClient.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // 自然语言结果总结模板
    private static final String RESULT_SUMMARY_TEMPLATE = """
    请将以下SQL查询结果总结为自然、友好的自然语言，模拟真实人类对话：
    
    原始查询：%s
    
    查询结果：%s
    
    要求：
    1. 使用亲切、自然的语气，就像朋友间的交流
    2. 用通俗易懂的语言描述，避免技术术语
    3. 保持客观，但可以适当加入温和的情感表达
    4. 如果结果为空，友好地说明没有找到相关信息
    5. 如果是更新/插入/删除操作，清楚说明操作成功和影响的行数
    6. 可以根据内容适当加入一些生活化的表达，让回复更生动
    7. 避免生硬的开头和结尾，让回复更流畅自然
    """;

    @Override
    public RestResult<?> handleAiChat(AiChatRequestDTO request) {
        try {
            // 校验用户是否存在
            UserDO user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                return RestResult.fail("用户不存在");
            }

            // 步骤1: 将自然语言转换为SQL
            RestResult<String> sqlResult = sqlGenerationService.generateSql(user, request.getMessage());
            if (!sqlResult.getCode().equals(200)) {
                return RestResult.fail("生成SQL失败: " + sqlResult.getMsg());
            }
            String sql = sqlResult.getData();

            // 步骤2: 安全验证
            if (!securityService.detectSqlInjection(sql)) {
                if (!securityService.validateUserPermission(user, sql)) {
                    return RestResult.fail("没有权限执行该操作");
                }
            } else {
                return RestResult.fail("SQL语句不安全，可能包含注入攻击");
            }

            // 步骤3: 执行SQL
            RestResult<?> executionResult;
            String sqlType = sql.trim().substring(0, 6).toUpperCase();
            
            if (sqlType.equals("SELECT")) {
                executionResult = sqlExecutionService.executeQuery(sql);
            } else if (sqlType.equals("INSERT") || sqlType.equals("UPDATE") || sqlType.equals("DELETE")) {
                executionResult = sqlExecutionService.executeUpdate(sql);
            } else {
                return RestResult.fail("不支持的SQL操作类型");
            }

            // 步骤4: 将结果转换为自然语言
            Map<String, Object> aiResponse = new HashMap<>();
            if (executionResult.getCode().equals(200)) {
                // 生成自然语言总结
                String summary = generateNaturalLanguageSummary(sql, executionResult.getData());
                aiResponse.put("reply", summary);
                aiResponse.put("sql", sql);
                aiResponse.put("result", executionResult.getData());
                aiResponse.put("timestamp", System.currentTimeMillis());
            } else {
                return RestResult.fail("执行SQL失败: " + executionResult.getMsg());
            }

            // 步骤5: 记录操作日志
            securityService.logSqlOperation(user, sql, aiResponse.get("reply").toString());

            return RestResult.success(aiResponse);
        } catch (Exception e) {
            log.error("处理AI聊天请求异常", e);
            // 返回更友好的错误提示
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("reply", "抱歉，我现在有点忙，暂时无法为您服务。请稍后再试哦~");
            errorResponse.put("timestamp", System.currentTimeMillis());
            return RestResult.success(errorResponse);
        }
    }

    /**
     * 将SQL执行结果转换为自然语言总结
     *
     * @param sql    原始SQL语句
     * @param result 执行结果
     * @return 自然语言总结
     */
    private String generateNaturalLanguageSummary(String sql, Object result) {
        try {
            // 创建完整的提示
            String prompt = String.format(RESULT_SUMMARY_TEMPLATE, sql, result.toString());
            
            // 调用Ollama API
            return callOllamaApi(prompt);
        } catch (Exception e) {
            log.error("生成自然语言总结失败", e);
            // 如果AI生成失败，返回更友好的原始结果提示
            return "查询到的信息是：" + result.toString() + "\n(系统有点忙碌，暂时用简洁方式为您呈现~)";
        }
    }
    
    /**
     * 调用Ollama API生成响应
     *
     * @param prompt 提示内容
     * @return AI生成的响应
     * @throws Exception 调用过程中发生的异常
     */
    private String callOllamaApi(String prompt) throws Exception {
        try {
            // 构建Ollama API请求体
            String requestBody = String.format("{\"model\": \"%s\", \"prompt\": %s, \"stream\": false}", 
                                              ollamaModel, objectMapper.writeValueAsString(prompt));
            
            // 发送POST请求到Ollama API
            String responseJson = webClient.post()
                    .uri("/api/generate")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // 解析JSON响应
            JsonNode rootNode = objectMapper.readTree(responseJson);
            return rootNode.get("response").asText();
        } catch (WebClientResponseException e) {
            log.error("Ollama API调用失败，状态码：{}，响应：{}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("调用Ollama API失败：" + e.getStatusCode());
        } catch (Exception e) {
            log.error("Ollama API调用异常：{}", e.getMessage(), e);
            throw new Exception("调用Ollama API异常：" + e.getMessage());
        }
    }
}