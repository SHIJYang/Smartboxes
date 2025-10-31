package org.example.boxes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "错误修复管理")
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
    @ApiOperation(value = "记录运行时错误")
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
    @ApiOperation(value = "触发优雅降级策略")
    public RestResult<Object> triggerGracefulFallback(@Valid @RequestBody FallbackRequestDTO requestDTO) {
        return fallbackService.triggerGracefulFallback(requestDTO);
    }
}