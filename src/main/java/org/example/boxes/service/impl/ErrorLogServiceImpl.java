package org.example.boxes.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.ErrorLogDTO;
import org.example.boxes.entity.ErrorLogDO;
import org.example.boxes.repository.ErrorLogRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ErrorLogService;
import org.springframework.stereotype.Service;

/**
 * 错误日志服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    @Override
    public RestResult<Void> recordRuntimeError(ErrorLogDTO errorLogDTO) {
        // 校验错误信息是否为空
        if (errorLogDTO.getErrorMessage() == null || errorLogDTO.getErrorMessage().isEmpty()) {
            log.warn("记录运行时错误失败：错误信息为空");
            return RestResult.error("错误信息不能为空");
        }

        try {
            // 将错误信息存储至错误日志中
            ErrorLogDO errorLogDO = new ErrorLogDO();
            errorLogDO.setErrorCode(errorLogDTO.getErrorCode());
            errorLogDO.setErrorMessage(errorLogDTO.getErrorMessage());
            errorLogDO.setErrorTime(errorLogDTO.getErrorTime());
            errorLogDO.setCreateTime(LocalDateTime.now());
            errorLogDO.setUpdateTime(LocalDateTime.now());

            errorLogRepository.save(errorLogDO);

            log.info("成功记录运行时错误，错误编码: {}", errorLogDTO.getErrorCode());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("记录运行时错误异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }
}