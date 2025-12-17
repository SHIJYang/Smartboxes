package org.example.boxes.service;

import org.example.boxes.entity.UserDO;

/**
 * 安全服务接口
 * 负责SQL安全验证和权限控制
 *
 * @author 14577
 */
public interface SecurityService {

    /**
     * 验证用户是否有权限执行SQL操作
     *
     * @param user 用户信息
     * @param sql  SQL语句
     * @return 是否有权限
     */
    boolean validateUserPermission(UserDO user, String sql);

    /**
     * 检查SQL语句是否包含注入攻击
     *
     * @param sql SQL语句
     * @return 是否安全
     */
    boolean detectSqlInjection(String sql);

    /**
     * 验证SQL语句是否符合允许的操作类型
     *
     * @param sql SQL语句
     * @return 是否允许
     */
    boolean validateSqlOperationType(String sql);

    /**
     * 记录SQL操作日志
     *
     * @param user 用户信息
     * @param sql  SQL语句
     * @param result 执行结果
     */
    void logSqlOperation(UserDO user, String sql, String result);

    /**
     * 密码加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    String encryptPassword(String password);

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encryptedPassword);

    /**
     * 生成认证令牌
     *
     * @param user 用户信息
     * @return 认证令牌
     */
    String generateToken(UserDO user);

    /**
     * 解析认证令牌
     *
     * @param token 认证令牌
     * @return 用户ID
     */
    Long parseToken(String token);
}
