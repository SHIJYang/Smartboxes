package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.LoginRequest;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关API")
public class UserController {
    @Autowired
    private final UserService userService;

    /**
     * 创建用户
     *
     * @param userDO 用户信息
     * @return 创建的用户DTO
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新的用户账户")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDO userDO) {
        UserDTO createdUser = userService.createUser(userDO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户详细信息")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    @GetMapping
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户列表")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 分页查询用户
     *
     * @param query 查询参数
     * @return 用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户信息")
    public ResponseEntity<List<UserDTO>> getUsersByPage(UserQuery query) {
        List<UserDTO> users = userService.getUsersByPage(query);
        return ResponseEntity.ok(users);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param userDO 用户信息
     * @return 更新后的用户DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户基本信息")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDO userDO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定的用户账户")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过账号密码登录，返回Token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 1. 执行登录逻辑
        UserDTO userDTO = userService.login(loginRequest.getUserAccount(), loginRequest.getUserPassword());

        // 2. 生成 Token
        String token = userService.generateToken(userDTO);

        // 3. 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userDTO);

        return ResponseEntity.ok(result);
    }
}