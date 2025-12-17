package org.example.boxes.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.entity.UserDO;
import org.example.boxes.service.SecurityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
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

    private final PasswordEncoder passwordEncoder;

    // 允许的SQL操作类型
    private static final List<String> ALLOWED_OPERATIONS = Arrays.asList("SELECT", "INSERT", "UPDATE", "DELETE");

    // JWT配置 (JJWT 0.12.x 推荐写法)
    // 使用 Jwts.SIG.HS512 生成符合安全标准的 SecretKey
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();

    private static final long EXPIRATION_TIME = 86400000; // 24小时（毫秒）

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
        // 建议：此处逻辑较为脆弱，对空格敏感。如果后续有复杂SQL，建议使用 JSqlParser 解析 Where 条件
        String userIdStr = String.valueOf(user.getId());
        // 简单清理空格以提高匹配率
        String normalizedSql = lowerSql.replace(" ", "");

        // 简单的字符串包含检查 (保留你原有的逻辑思路，稍微增强一点鲁棒性)
        boolean hasAuthCheck = lowerSql.contains("user_id=" + userIdStr) ||
                lowerSql.contains("user_id =" + userIdStr) ||
                lowerSql.contains("user_id= " + userIdStr) ||
                lowerSql.contains("user_id = " + userIdStr) ||
                lowerSql.contains("user_id=\"" + userIdStr + "\"") ||
                lowerSql.contains("user_id='" + userIdStr + "'");

        if (!hasAuthCheck) {
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
        log.info("用户SQL操作 - 用户ID: {}, 用户名: {}, SQL: {}, 结果: {}",
                user.getId(),
                user.getUsername() != null ? user.getUsername() : "Unknown",
                sql, result);
    }

    @Override
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    @Override
    public String generateToken(UserDO user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        // JJWT 0.12.x 写法
        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(SECRET_KEY) // 直接传入 Key 对象，无需指定算法，它会自动识别
                .compact();
    }

    @Override
    public Long parseToken(String token) {
        // JJWT 0.12.x 写法
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY) // 替代 setSigningKey
                .build()                // 必须调用 build()
                .parseSignedClaims(token) // 替代 parseClaimsJws
                .getPayload();          // 替代 getBody()

        return Long.parseLong(claims.getSubject());
    }
}