package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.SecurityService;
import org.example.boxes.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    public RestResult<UserDTO> createUser(UserDO userDO) {
        try {
            // 1. 检查账号是否存在
            if (userRepository.findByUserAccount(userDO.getUserAccount()) != null) {
                return RestResult.fail("该账号已存在");
            }

            // 2. 密码加密
            String rawPassword = userDO.getUserPassword();
            userDO.setUserPassword(securityService.encryptPassword(rawPassword));

            // 3. 设置默认时间
            userDO.setCreateTime(LocalDateTime.now());
            userDO.setUpdateTime(LocalDateTime.now());

            UserDO savedUser = userRepository.save(userDO);
            return RestResult.success(convertToDTO(savedUser));
        } catch (Exception e) {
            log.error("创建用户失败: ", e);
            return RestResult.fail("创建用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<UserDTO> getUserById(Long id) {
        try {
            UserDO userDO = userRepository.findById(id)
                    .orElse(null);

            if (userDO == null) {
                return RestResult.fail("用户不存在");
            }

            return RestResult.success(convertToDTO(userDO));
        } catch (Exception e) {
            log.error("获取用户失败: ", e);
            return RestResult.fail("获取用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return RestResult.success(users);
        } catch (Exception e) {
            log.error("获取所有用户失败: ", e);
            return RestResult.fail("获取所有用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<List<UserDTO>> getUsersByPage(UserQuery query) {
        try {
            // 注意：这里需要实现分页查询逻辑
            // 暂时先返回所有用户，实际应根据query参数实现分页
            List<UserDTO> users = userRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return RestResult.success(users);
        } catch (Exception e) {
            log.error("分页查询用户失败: ", e);
            return RestResult.fail("分页查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<UserDTO> updateUser(Long id, UserDO userDO) {
        try {
            UserDO existingUser = userRepository.findById(id)
                    .orElse(null);

            if (existingUser == null) {
                return RestResult.fail("用户不存在");
            }

            existingUser.setUsername(userDO.getUsername());
            existingUser.setPhone(userDO.getPhone());
            existingUser.setUpdateTime(LocalDateTime.now());

            // 如果要更新密码，也需要加密处理
            if (userDO.getUserPassword() != null && !userDO.getUserPassword().isEmpty()) {
                existingUser.setUserPassword(securityService.encryptPassword(userDO.getUserPassword()));
            }

            UserDO updatedUser = userRepository.save(existingUser);
            return RestResult.success(convertToDTO(updatedUser));
        } catch (Exception e) {
            log.error("更新用户失败: ", e);
            return RestResult.fail("更新用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<Void> deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return RestResult.fail("用户不存在");
            }

            userRepository.deleteById(id);
            return RestResult.success();
        } catch (Exception e) {
            log.error("删除用户失败: ", e);
            return RestResult.fail("删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<Map<String, Object>> login(String userAccount, String password) {
        try {
            // 1. 根据账号查询用户
            UserDO userDO = userRepository.findByUserAccount(userAccount);
            if (userDO == null) {
                // 实际生产建议模糊提示"账号或密码错误"
                return RestResult.fail("账号或密码错误");
            }

            // 2. 校验密码
            if (!securityService.verifyPassword(password, userDO.getUserPassword())) {
                return RestResult.fail("账号或密码错误");
            }

            // 3. 生成Token
            UserDTO userDTO = convertToDTO(userDO);
            String token = generateToken(userDTO);

            // 4. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", userDTO);

            return RestResult.success(result);
        } catch (Exception e) {
            log.error("用户登录失败: ", e);
            return RestResult.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 原有的generateToken方法，现在被login方法调用
     */
    private String generateToken(UserDTO userDTO) {
        UserDO userDO = new UserDO();
        userDO.setId(userDTO.getId());
        userDO.setUsername(userDTO.getUsername());
        return securityService.generateToken(userDO);
    }

    /**
     * 转换DO到DTO
     */
    private UserDTO convertToDTO(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDO.getId());
        userDTO.setUsername(userDO.getUsername());
        userDTO.setEmail(userDO.getUserAccount()); // 暂时映射Account到Email字段
        userDTO.setCreatedAt(userDO.getCreateTime());
        userDTO.setUpdatedAt(userDO.getUpdateTime());
        return userDTO;
    }
}