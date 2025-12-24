package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.LoginRequest;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关API")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     *
     * @param userDO 用户信息
     * @return RestResult 结果封装
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新的用户账户")
    public ResponseEntity<RestResult<UserDTO>> createUser(@Valid @RequestBody UserDO userDO) {
        return ResponseEntity.ok(userService.createUser(userDO));
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return RestResult 结果封装
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户详细信息")
    public ResponseEntity<RestResult<UserDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * 获取所有用户
     *
     * @return RestResult 结果封装
     */
    @GetMapping
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户列表")
    public ResponseEntity<RestResult<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * 分页查询用户
     *
     * @param query 查询参数
     * @return RestResult 结果封装
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户信息")
    public ResponseEntity<RestResult<List<UserDTO>>> getUsersByPage(UserQuery query) {
        return ResponseEntity.ok(userService.getUsersByPage(query));
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param userDO 用户信息
     * @return RestResult 结果封装
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户基本信息")
    public ResponseEntity<RestResult<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDO userDO) {
        return ResponseEntity.ok(userService.updateUser(id, userDO));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return RestResult 结果封装
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定的用户账户")
    public ResponseEntity<RestResult<Void>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return RestResult 结果封装
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过账号密码登录，返回Token")
    public ResponseEntity<RestResult<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        RestResult<Map<String, Object>> result = userService.login(
                loginRequest.getUserAccount(),
                loginRequest.getUserPassword()
        );
        return ResponseEntity.ok(result);
    }
}