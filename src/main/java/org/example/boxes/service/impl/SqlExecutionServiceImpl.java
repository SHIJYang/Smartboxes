package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.SqlExecutionService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL执行服务实现类
 * 使用JdbcTemplate安全地执行SQL语句并处理结果
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SqlExecutionServiceImpl implements SqlExecutionService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public RestResult<List<Map<String, Object>>> executeQuery(String sql) {
        try {
            log.info("执行查询SQL: {}", sql);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            log.info("查询结果: {}条记录", result.size());
            return RestResult.success(result);
        } catch (Exception e) {
            log.error("执行查询SQL失败: {}", e.getMessage(), e);
            return RestResult.fail("执行查询失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<Integer> executeUpdate(String sql) {
        try {
            log.info("执行更新SQL: {}", sql);
            int affectedRows = jdbcTemplate.update(sql);
            log.info("更新完成，影响行数: {}", affectedRows);
            return RestResult.success(affectedRows);
        } catch (Exception e) {
            log.error("执行更新SQL失败: {}", e.getMessage(), e);
            return RestResult.fail("执行更新失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<List<Map<String, Object>>> executeParamQuery(String sql, Object... params) {
        try {
            log.info("执行参数化查询SQL: {}, 参数: {}", sql, params);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);
            log.info("查询结果: {}条记录", result.size());
            return RestResult.success(result);
        } catch (Exception e) {
            log.error("执行参数化查询SQL失败: {}", e.getMessage(), e);
            return RestResult.fail("执行参数化查询失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<Integer> executeParamUpdate(String sql, Object... params) {
        try {
            log.info("执行参数化更新SQL: {}, 参数: {}", sql, params);
            int affectedRows = jdbcTemplate.update(sql, params);
            log.info("更新完成，影响行数: {}", affectedRows);
            return RestResult.success(affectedRows);
        } catch (Exception e) {
            log.error("执行参数化更新SQL失败: {}", e.getMessage(), e);
            return RestResult.fail("执行参数化更新失败: " + e.getMessage());
        }
    }
}
