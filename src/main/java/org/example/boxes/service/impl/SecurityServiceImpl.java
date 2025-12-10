package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.entity.UserDO;
import org.example.boxes.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 安全服务实现类
 * 负责SQL安全验证和权限控制
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    // 允许的SQL操作类型
    private static final List<String> ALLOWED_OPERATIONS = Arrays.asList("SELECT", "INSERT", "UPDATE", "DELETE");

    // 禁止的SQL关键字
    private static final List<String> FORBIDDEN_KEYWORDS = Arrays.asList(
            "DROP", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE",
            "EXEC", "EXECUTE", "XP_CMDSHELL", "SP_", "LOAD_FILE", "INTO OUTFILE"
    );

    // SQL注入模式
    private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
            Pattern.compile("'.*--", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'.*' OR '1'='1'", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'.*' UNION ", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'.*'; DROP ", Pattern.CASE_INSENSITIVE),
            Pattern.compile("';.*--", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public boolean validateUserPermission(UserDO user, String sql) {
        if (user == null || sql == null) {
            return false;
        }

        // 检查SQL是否包含user_id过滤
        String lowerSql = sql.toLowerCase();
        if (!lowerSql.contains("user_id")) {
            log.warn("SQL未包含user_id过滤: {}", sql);
            return false;
        }

        // 检查是否过滤了当前用户的数据
        if (!lowerSql.contains("user_id = " + user.getId()) && 
            !lowerSql.contains("user_id= " + user.getId()) &&
            !lowerSql.contains("user_id =" + user.getId()) &&
            !lowerSql.contains("user_id=\"" + user.getId() + "\"") &&
            !lowerSql.contains("user_id =\"" + user.getId() + "\"")) {
            log.warn("SQL未正确过滤当前用户数据: {}, 用户ID: {}", sql, user.getId());
            return false;
        }

        return true;
    }

    @Override
    public boolean detectSqlInjection(String sql) {
        if (sql == null) {
            return false;
        }

        String lowerSql = sql.toLowerCase();

        // 检查禁止的关键字
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (lowerSql.contains(keyword.toLowerCase())) {
                log.warn("SQL包含禁止的关键字: {}, 关键字: {}", sql, keyword);
                return true;
            }
        }

        // 检查注入模式
        for (Pattern pattern : INJECTION_PATTERNS) {
            Matcher matcher = pattern.matcher(sql);
            if (matcher.find()) {
                log.warn("SQL包含注入模式: {}, 匹配: {}", sql, matcher.group());
                return true;
            }
        }

        // 检查是否包含多个SQL语句
        if (sql.trim().endsWith(";") && sql.split(";", -1).length > 2) {
            log.warn("SQL包含多个语句，可能存在注入风险: {}", sql);
            return true;
        }

        return false;
    }

    @Override
    public boolean validateSqlOperationType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        String upperSql = sql.trim().toUpperCase();

        // 检查是否以允许的操作开头
        for (String operation : ALLOWED_OPERATIONS) {
            if (upperSql.startsWith(operation)) {
                return true;
            }
        }

        log.warn("SQL操作类型不被允许: {}", sql);
        return false;
    }

    @Override
    public void logSqlOperation(UserDO user, String sql, String result) {
        // 记录SQL操作日志
        log.info("用户SQL操作 - 用户ID: {}, 用户名: {}, SQL: {}, 结果: {}",
                user.getId(), user.getUsername() != null ? user.getUsername() : user.getUserAccount(),
                sql, result);
    }
}
