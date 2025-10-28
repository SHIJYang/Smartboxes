package org.example.boxes.service;

import org.example.boxes.dto.FallbackRequestDTO;
import org.example.boxes.result.RestResult;

/**
 * 优雅降级服务接口
 *
 * @author 14577
 */
public interface FallbackService {

    /**
     * 触发优雅降级策略
     *
     * @param requestDTO 降级请求数据传输对象
     * @return RestResult 结果封装
     */
    RestResult<Object> triggerGracefulFallback(FallbackRequestDTO requestDTO);
}