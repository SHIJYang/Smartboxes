package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.ErrorLogDTO;
import org.example.boxes.dto.FallbackRequestDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ErrorLogService;
import org.example.boxes.service.FallbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误修复管理控制器
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/error-recovery")
@Tag(name = "错误修复管理", description = "错误日志和降级管理API")
@RequiredArgsConstructor
public class ErrorRecoveryController {

    private final ErrorLogService errorLogService;
    private final FallbackService fallbackService;

    /**
     * 记录运行时错误
     *
     * @param errorLogDTO 错误日志数据传输对象
     * @return RestResult 结果封装
     */
    @PostMapping("/record-error")
    @Operation(summary = "记录运行时错误", description = "记录系统运行时错误日志")
    public RestResult<Void> recordRuntimeError(@Valid @RequestBody ErrorLogDTO errorLogDTO) {
        return errorLogService.recordRuntimeError(errorLogDTO);
    }

    /**
     * 触发优雅降级策略
     *
     * @param requestDTO 降级请求数据传输对象
     * @return RestResult 结果封装
     */
    @PostMapping("/trigger-fallback")
    @Operation(summary = "触发优雅降级策略", description = "在服务异常时触发降级策略")
    public RestResult<Object> triggerGracefulFallback(@Valid @RequestBody FallbackRequestDTO requestDTO) {
        return fallbackService.triggerGracefulFallback(requestDTO);
    }
}