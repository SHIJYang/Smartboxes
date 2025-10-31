package org.example.boxes.service;

import org.example.boxes.dto.ErrorLogDTO;
import org.example.boxes.result.RestResult;

/**
 * 错误日志服务接口
 *
 * @author 14577
 */
public interface ErrorLogService {

    /**
     * 记录运行时错误
     *
     * @param errorLogDTO 错误日志数据传输对象
     * @return RestResult 结果封装
     */
    RestResult<Void> recordRuntimeError(ErrorLogDTO errorLogDTO);
}