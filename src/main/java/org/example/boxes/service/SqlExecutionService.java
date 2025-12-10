package org.example.boxes.service;

import org.example.boxes.result.RestResult;

import java.util.List;
import java.util.Map;

/**
 * SQL执行服务接口
 * 负责安全地执行SQL语句并处理结果
 *
 * @author 14577
 */
public interface SqlExecutionService {

    /**
     * 执行SQL查询语句
     *
     * @param sql SQL查询语句
     * @return 查询结果列表
     */
    RestResult<List<Map<String, Object>>> executeQuery(String sql);

    /**
     * 执行SQL更新语句（INSERT、UPDATE、DELETE）
     *
     * @param sql SQL更新语句
     * @return 影响的行数
     */
    RestResult<Integer> executeUpdate(String sql);

    /**
     * 执行参数化SQL查询
     *
     * @param sql    SQL查询语句
     * @param params 参数列表
     * @return 查询结果列表
     */
    RestResult<List<Map<String, Object>>> executeParamQuery(String sql, Object... params);

    /**
     * 执行参数化SQL更新
     *
     * @param sql    SQL更新语句
     * @param params 参数列表
     * @return 影响的行数
     */
    RestResult<Integer> executeParamUpdate(String sql, Object... params);
}
