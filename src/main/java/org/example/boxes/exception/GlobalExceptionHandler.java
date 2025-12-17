package org.example.boxes.exception; // 记得修改包名

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        // 捕获 "账号不存在" 或 "密码错误" 等运行时异常
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400); // 业务状态码
        result.put("msg", e.getMessage()); // 返回 "账号不存在" 给前端
        result.put("data", null);

        // 返回 HTTP 400 Bad Request 而不是 500 Internal Server Error
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}