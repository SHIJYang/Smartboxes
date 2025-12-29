package org.example.boxes.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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

    // 普通聊天模板
    private static final String NORMAL_CHAT_TEMPLATE = """
    请以友好、自然的方式回复用户的消息，就像朋友间的聊天一样。
    
    用户消息：%s
    
    要求：
    1. 保持亲切、友善的语气
    2. 根据用户消息的内容进行自然回应
    3. 如果用户询问你的能力，可以简要说明你能帮助查询数据库信息
    4. 避免过于机械的回复
    5. 可以适当加入表情符号让对话更生动
    6. 如果不确定用户意图，可以友好地询问澄清
    
    请注意：用户现在只是在进行普通聊天，不需要查询数据库。
    """;

    // 数据库查询意图识别提示
    private static final String INTENT_DETECTION_TEMPLATE = """
    请分析用户的这句话是否是在请求查询数据库信息（比如找寻物品、查看数据、统计信息等）。
    
    用户消息：%s
    
    分析要求：
    1. 如果用户明显在请求查询数据库中的信息（如：我的物品有哪些、查看我的数据、统计数量等），回答：DATABASE_QUERY
    2. 如果用户只是在普通聊天（如：问候、闲聊、情感交流等），回答：NORMAL_CHAT
    3. 如果不确定，但可能涉及数据查询，回答：DATABASE_QUERY
    
    只回答 DATABASE_QUERY 或 NORMAL_CHAT，不要添加其他内容。
    """;

    // 数据库查询关键词（中文）
    private static final String[] DATABASE_QUERY_KEYWORDS_CN = {
            "查", "找", "搜索", "看看", "有没有", "是多少", "统计", "我的", "物品",
            "数据", "信息", "记录", "详情", "列表", "多少", "几个", "数量", "查看",
            "显示", "展示", "查询", "查找", "搜索", "检索", "获取", "获得", "拿到",
            "物品在哪", "我的东西", "我的记录", "我的数据", "帮我找", "我想看", "我要看"
    };

    // 普通聊天关键词（中文）
    private static final String[] NORMAL_CHAT_KEYWORDS_CN = {
            "你好", "嗨", "在吗", "干嘛呢", "最近怎样", "谢谢", "感谢", "不客气",
            "拜拜", "再见", "晚安", "早上好", "下午好", "晚上好", "天气", "心情",
            "开心", "难过", "哈哈", "嘿嘿", "嘻嘻", "笑", "哭", "生气", "喜欢",
            "爱", "讨厌", "怎么办", "为什么", "如何", "怎样", "建议", "帮忙",
            "聊天", "聊聊", "说话", "对话", "讲个笑话", "故事", "新闻", "热点"
    };

    // 数据库查询模式（正则表达式）
    private static final Pattern[] DATABASE_QUERY_PATTERNS = {
            Pattern.compile("(我的|我有|我要|我想).*(物品|东西|数据|信息|记录)"),
            Pattern.compile("(查看|查询|查找|搜索|统计).*"),
            Pattern.compile("(有多少|几个|多少).*(物品|东西|记录)"),
            Pattern.compile("(物品|东西|数据).*(在哪|在哪里|有多少)"),
            Pattern.compile("^(帮我|请|麻烦).*(查|找|搜索)"),
            Pattern.compile("(显示|展示|列出|列举).*(列表|清单)")
    };

    @Override
    public RestResult<?> handleAiChat(AiChatRequestDTO request) {
        try {
            // 校验用户是否存在
            UserDO user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                return RestResult.fail("用户不存在");
            }

            String message = request.getMessage();
            String intent = detectUserIntent(message);

            log.info("用户意图识别 - 用户ID: {}, 消息: {}, 识别结果: {}",
                    user.getId(), message, intent);

            Map<String, Object> response = new HashMap<>();

            if ("DATABASE_QUERY".equals(intent)) {
                // 数据库查询
                try {
                    response = handleDatabaseQuery(user, message);
                } catch (Exception e) {
                    log.warn("数据库查询失败，转为普通聊天: {}", e.getMessage());
                    // 如果数据库查询失败，转为普通聊天
                    String reply = generateNormalChatResponse(message);
                    response.put("reply", reply + "\n\n（我本想帮你查询信息，但暂时遇到点小问题，我们可以先聊聊天~）");
                    response.put("type", "normal_chat_fallback");
                    response.put("timestamp", System.currentTimeMillis());
                }
            } else {
                // 普通聊天
                String reply = generateNormalChatResponse(message);
                response.put("reply", reply);
                response.put("type", "normal_chat");
                response.put("timestamp", System.currentTimeMillis());

                // 记录普通聊天日志
                securityService.logSqlOperation(user, "NORMAL_CHAT", reply);
            }

            return RestResult.success(response);

        } catch (Exception e) {
            log.error("处理AI聊天请求异常", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("reply", "抱歉，我现在有点忙，暂时无法为您服务。请稍后再试哦~ 😊");
            errorResponse.put("timestamp", System.currentTimeMillis());
            return RestResult.success(errorResponse);
        }
    }

    /**
     * 检测用户意图
     * 使用规则+AI双重判断
     */
    private String detectUserIntent(String message) {
        // 1. 规则匹配（快速判断）
        String ruleBasedIntent = detectIntentByRules(message);

        // 如果规则匹配明确，直接返回
        if (!"UNCERTAIN".equals(ruleBasedIntent)) {
            return ruleBasedIntent;
        }

        // 2. AI判断（对不确定的情况）
        try {
            return detectIntentByAI(message);
        } catch (Exception e) {
            log.warn("AI意图识别失败，使用规则结果: {}", e.getMessage());
            // AI失败时，保守地当做普通聊天处理
            return "NORMAL_CHAT";
        }
    }

    /**
     * 基于规则的意图检测
     */
    private String detectIntentByRules(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "NORMAL_CHAT";
        }

        String lowerMessage = message.toLowerCase().trim();
        int queryScore = 0;
        int chatScore = 0;

        // 检查数据库查询关键词
        for (String keyword : DATABASE_QUERY_KEYWORDS_CN) {
            if (lowerMessage.contains(keyword)) {
                queryScore++;
            }
        }

        // 检查正则表达式模式
        for (Pattern pattern : DATABASE_QUERY_PATTERNS) {
            Matcher matcher = pattern.matcher(lowerMessage);
            if (matcher.find()) {
                queryScore += 2; // 模式匹配权重更高
            }
        }

        // 检查普通聊天关键词
        for (String keyword : NORMAL_CHAT_KEYWORDS_CN) {
            if (lowerMessage.contains(keyword)) {
                chatScore++;
            }
        }

        // 判断是否是问候开头
        if (lowerMessage.startsWith("你好") || lowerMessage.startsWith("嗨") ||
                lowerMessage.startsWith("hello") || lowerMessage.startsWith("hi")) {
            chatScore += 2;
        }

        // 判断结果
        if (queryScore > chatScore && queryScore >= 2) {
            return "DATABASE_QUERY";
        } else if (chatScore > queryScore && chatScore >= 2) {
            return "NORMAL_CHAT";
        } else {
            return "UNCERTAIN"; // 不确定
        }
    }

    /**
     * 使用AI进行意图识别
     */
    private String detectIntentByAI(String message) throws Exception {
        String prompt = String.format(INTENT_DETECTION_TEMPLATE, message);

        String response = callOllamaApi(prompt);
        String trimmedResponse = response.trim().toUpperCase();

        if (trimmedResponse.contains("DATABASE_QUERY")) {
            return "DATABASE_QUERY";
        } else if (trimmedResponse.contains("NORMAL_CHAT")) {
            return "NORMAL_CHAT";
        } else {
            // AI回复不符合预期，使用规则
            log.warn("AI意图识别返回异常: {}", response);
            return detectIntentByRules(message);
        }
    }

    /**
     * 生成普通聊天回复
     */
    private String generateNormalChatResponse(String message) {
        try {
            String prompt = String.format(NORMAL_CHAT_TEMPLATE, message);
            return callOllamaApi(prompt);
        } catch (Exception e) {
            log.error("生成普通聊天回复失败", e);
            // 备用回复
            return getFallbackChatResponse(message);
        }
    }

    /**
     * 备用聊天回复（当AI调用失败时）
     */
    private String getFallbackChatResponse(String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("你好") || lowerMessage.contains("hi") || lowerMessage.contains("hello")) {
            return "你好呀！😊 很高兴见到你！";
        } else if (lowerMessage.contains("谢谢") || lowerMessage.contains("感谢")) {
            return "不客气～ 能帮到你就好！";
        } else if (lowerMessage.contains("拜拜") || lowerMessage.contains("再见")) {
            return "再见啦！随时欢迎找我聊天哦～ 👋";
        } else if (lowerMessage.contains("天气")) {
            return "今天天气不错呢！☀️ 适合出门走走～";
        } else if (lowerMessage.contains("心情")) {
            return "希望你今天有个好心情！😄 有什么想聊的都可以和我说～";
        } else {
            return "我明白你的意思了！😊 如果你需要查询物品信息，也可以告诉我哦～";
        }
    }

    /**
     * 处理数据库查询（原有逻辑，稍作调整）
     */
    private Map<String, Object> handleDatabaseQuery(UserDO user, String message) throws Exception {
        // 步骤1: 将自然语言转换为SQL
        RestResult<String> sqlResult = sqlGenerationService.generateSql(user, message);
        if (!sqlResult.getCode().equals(200)) {
            throw new Exception("生成SQL失败: " + sqlResult.getMsg());
        }
        String sql = sqlResult.getData();

        // 步骤2: 安全验证
        if (securityService.detectSqlInjection(sql)) {
            throw new Exception("SQL语句不安全，可能包含注入攻击");
        }

        if (!securityService.validateUserPermission(user, sql)) {
            throw new Exception("没有权限执行该操作");
        }

        // 步骤3: 执行SQL
        RestResult<?> executionResult;
        String upperSql = sql.trim().toUpperCase();

        if (upperSql.startsWith("SELECT")) {
            executionResult = sqlExecutionService.executeQuery(sql);
        } else if (upperSql.startsWith("INSERT") || upperSql.startsWith("UPDATE") || upperSql.startsWith("DELETE")) {
            executionResult = sqlExecutionService.executeUpdate(sql);
        } else {
            throw new Exception("不支持的SQL操作类型");
        }

        // 步骤4: 将结果转换为自然语言
        Map<String, Object> aiResponse = new HashMap<>();
        if (executionResult.getCode().equals(200)) {
            String summary = generateNaturalLanguageSummary(sql, executionResult.getData());
            aiResponse.put("reply", summary);
            aiResponse.put("sql", sql);
            aiResponse.put("result", executionResult.getData());
            aiResponse.put("type", "database_query");
            aiResponse.put("timestamp", System.currentTimeMillis());
        } else {
            throw new Exception("执行SQL失败: " + executionResult.getMsg());
        }

        // 步骤5: 记录操作日志
        securityService.logSqlOperation(user, sql, aiResponse.get("reply").toString());

        return aiResponse;
    }

    /**
     * 将SQL执行结果转换为自然语言总结
     */
    private String generateNaturalLanguageSummary(String sql, Object result) {
        try {
            String prompt = String.format(RESULT_SUMMARY_TEMPLATE, sql, result.toString());
            return callOllamaApi(prompt);
        } catch (Exception e) {
            log.error("生成自然语言总结失败", e);
            return "查询到的信息是：" + result.toString() + "\n(系统有点忙碌，暂时用简洁方式为您呈现~)";
        }
    }

    /**
     * 调用Ollama API生成响应
     */
    private String callOllamaApi(String prompt) throws Exception {
        try {
            String requestBody = String.format("{\"model\": \"%s\", \"prompt\": %s, \"stream\": false}",
                    ollamaModel, objectMapper.writeValueAsString(prompt));

            String responseJson = webClient.post()
                    .uri("/api/generate")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

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