package org.example.boxes.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.boxes.entity.UserDO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.SqlGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL生成服务实现类
 * 使用AI将自然语言转换为SQL语句
 *
 * @author 14577
 */
@Slf4j
@Service
public class SqlGenerationServiceImpl implements SqlGenerationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    // Ollama API 配置
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;
    
    // 构造函数注入
    public SqlGenerationServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // 数据库表结构描述，用于AI生成正确的SQL
    private static final String DATABASE_SCHEMA = """
    数据库表结构说明：
    
    1. t_user（用户表）
       - id: BIGINT, 主键
       - user_account: VARCHAR(255), 用户名
       - user_password: VARCHAR(255), 密码
       - username: VARCHAR(255), 真实姓名
       - phone: VARCHAR(255), 手机号
       - create_time: DATETIME, 创建时间
       - update_time: DATETIME, 更新时间
    
    2. t_box（箱体表）
       - id: BIGINT, 主键
       - box_code: VARCHAR(255), 箱体编号
       - user_id: BIGINT, 用户ID
       - box_name: VARCHAR(255), 箱体名称
       - box_type: INT, 箱体类型（0-子箱，1-主箱）
       - status: INT, 状态
       - rssi: INT, 信号强度
       - battery: INT, 电量
       - last_heartbeat_time: DATETIME, 最后心跳时间
       - create_time: DATETIME, 创建时间
       - update_time: DATETIME, 更新时间
    
    3. t_item（物品表）
       - id: BIGINT, 主键
       - item_code: VARCHAR(255), 物品编号
       - box_id: BIGINT, 箱体ID
       - auto_recognize_name: VARCHAR(255), 自动识别名称
       - manual_edit_name: VARCHAR(255), 手动编辑名称
       - item_tag: VARCHAR(255), 物品标签
       - item_desc: TEXT, 物品描述
       - put_in_time: DATETIME, 放入时间
       - expire_time: DATETIME, 过期时间
       - is_valid: INT, 是否有效
       - create_time: DATETIME, 创建时间
       - update_time: DATETIME, 更新时间
    
    4. t_emotion（情感表）
       - id: BIGINT, 主键
       - item_id: BIGINT, 物品ID
       - emotion_score: INT, 情感分数
       - emotion_desc: TEXT, 情感描述
       - create_time: DATETIME, 创建时间
       - update_time: DATETIME, 更新时间
    
    5. t_item_operate_log（物品操作日志表）
       - id: BIGINT, 主键
       - item_id: BIGINT, 物品ID
       - operate_type: INT, 操作类型
       - operate_desc: TEXT, 操作描述
       - operate_time: DATETIME, 操作时间
       - operator_id: BIGINT, 操作员ID
    
    6. t_box_switch_log（箱体开关日志表）
       - id: BIGINT, 主键
       - box_id: BIGINT, 箱体ID
       - switch_status: INT, 开关状态
       - switch_time: DATETIME, 开关时间
    
    注意事项：
    1. 只能操作以上表，不能使用其他表
    2. 只能执行SELECT、INSERT、UPDATE、DELETE语句
    3. 必须过滤当前用户的数据，通过user_id字段关联
    4. 生成的SQL必须是标准的MySQL语法
    5. 只返回SQL语句，不要包含其他解释
    """;

    // SQL生成的提示模板
    private static final String SQL_PROMPT_TEMPLATE = """
    请将以下自然语言查询转换为SQL语句，基于给定的数据库表结构：
    
    %s
    
    用户ID：%d
    
    查询：%s
    
    要求：
    1. 必须严格基于给定的表结构
    2. 必须过滤当前用户的数据
    3. 只返回SQL语句，不要包含其他内容
    4. SQL语句必须是有效的MySQL语法
    5. 对于插入、更新、删除操作，必须确保用户有权限
    6. 确保SQL语句的安全性，避免注入风险
    """;

    @Override
    public RestResult<String> generateSql(UserDO user, String question) {
        try {
            // 创建完整的提示
            String prompt = String.format(SQL_PROMPT_TEMPLATE, DATABASE_SCHEMA, user.getId(), question);
            
            // 调用Ollama API
            String aiResponse = callOllamaApi(prompt);

            // 提取SQL语句（移除可能的Markdown格式）
            String sql = extractSqlFromResponse(aiResponse);

            // 验证SQL安全性
            if (!validateSql(sql)) {
                return RestResult.fail("生成的SQL语句不安全");
            }

            log.info("生成SQL成功: {}", sql);
            return RestResult.success(sql);
        } catch (Exception e) {
            log.error("生成SQL失败: {}", e.getMessage(), e);
            return RestResult.fail("生成SQL失败: " + e.getMessage());
        }
    }

    @Override
    public boolean validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        String lowerSql = sql.toLowerCase();

        // 检查是否是支持的SQL语句类型（必须以SELECT、INSERT、UPDATE或DELETE开头）
        if (!lowerSql.startsWith("select ") && !lowerSql.startsWith("insert ") && 
            !lowerSql.startsWith("update ") && !lowerSql.startsWith("delete ")) {
            log.warn("SQL必须以SELECT、INSERT、UPDATE或DELETE开头");
            return false;
        }

        // 禁止的SQL关键字和操作（数据定义和控制语句）
        String[] forbiddenKeywords = {
                "drop", "truncate", "alter", "create", "grant", "revoke",
                "exec", "execute", "xp_cmdshell", "sp_"
        };

        // 检查是否包含禁止的关键字
        for (String keyword : forbiddenKeywords) {
            if (lowerSql.contains(keyword)) {
                log.warn("SQL包含禁止的关键字: {}", keyword);
                return false;
            }
        }

        // 检查是否包含危险的字符（除了分号）
        String[] dangerousChars = {"'", "\"", "--", "/*", "*/"};
        for (String charSeq : dangerousChars) {
            if (sql.contains(charSeq)) {
                log.warn("SQL包含危险字符: {}", charSeq);
                return false;
            }
        }
        
        // 检查是否包含多个SQL语句（通过分号判断）
        if (sql.trim().endsWith(";") && sql.split(";", -1).length > 2) {
            log.warn("SQL包含多个语句，可能存在注入风险");
            return false;
        }

        return true;
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
    
    /**
     * 从AI响应中提取SQL语句
     * 移除可能的Markdown格式
     *
     * @param response AI响应内容
     * @return 提取的SQL语句
     */
    private String extractSqlFromResponse(String response) {
        // 移除可能的SQL代码块标记
        response = response.replace("```sql", "").replace("```", "").trim();
        
        // 提取SQL语句（假设响应中只包含一个SQL语句）
        return response;
    }
}
