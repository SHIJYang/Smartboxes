package org.example.boxes.service.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.FallbackRequestDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.FallbackService;
import org.springframework.stereotype.Service;

/**
 * 优雅降级服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
public class FallbackServiceImpl implements FallbackService {

    @Override
    public RestResult<Object> triggerGracefulFallback(FallbackRequestDTO requestDTO) {
        // 判断是否存在可用的服务实例（模拟）
        boolean hasAvailableInstance = checkServiceAvailability(requestDTO.getServiceId());

        if (!hasAvailableInstance) {
            log.warn("触发优雅降级策略失败：没有可用的服务实例，服务标识: {}", requestDTO.getServiceId());
            return RestResult.error("调用失败，无可用服务实例");
        }

        try {
            // 执行预设的降级逻辑并返回备用数据
            Object fallbackData = executeFallbackLogic(requestDTO);

            log.info("成功触发优雅降级策略，服务标识: {}, 降级原因: {}", 
                    requestDTO.getServiceId(), requestDTO.getFallbackReason());
            return RestResult.success(fallbackData);
        } catch (Exception e) {
            log.error("执行优雅降级策略异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 检查服务可用性（模拟方法）
     *
     * @param serviceId 服务标识
     * @return 是否存在可用实例
     */
    private boolean checkServiceAvailability(String serviceId) {
        // 这里可以加入真正的健康检查逻辑
        // 当前为简单模拟
        return !"SERVICE_001".equals(serviceId); // SERVICE_001 不可用
    }

    /**
     * 执行降级逻辑（模拟方法）
     *
     * @param requestDTO 降级请求数据传输对象
     * @return 备用数据
     */
    private Object executeFallbackLogic(FallbackRequestDTO requestDTO) {
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("message", "当前服务不可用，正在使用降级方案");
        fallbackData.put("serviceId", requestDTO.getServiceId());
        fallbackData.put("fallbackReason", requestDTO.getFallbackReason());
        fallbackData.put("timestamp", System.currentTimeMillis());
        return fallbackData;
    }
}