package org.example.boxes.service;

import org.example.boxes.entity.UserDO;
import org.example.boxes.result.RestResult;

/**
 * SQL生成服务接口
 * 负责将自然语言转换为SQL语句
 *
 * @author 14577
 */
public interface SqlGenerationService {

    /**
     * 将自然语言转换为SQL语句
     *
     * @param user     用户信息
     * @param question 自然语言查询
     * @return 生成的SQL语句
     */
    RestResult<String> generateSql(UserDO user, String question);

    /**
     * 验证生成的SQL语句是否安全
     *
     * @param sql SQL语句
     * @return 是否安全
     */
    boolean validateSql(String sql);
}
