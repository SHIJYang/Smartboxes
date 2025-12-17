package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.service.SecurityService; // 引入之前写的安全服务
import org.example.boxes.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService; // 注入安全服务

    @Override
    public UserDTO createUser(UserDO userDO) {
        // 1. 检查账号是否存在
        if (userRepository.findByUserAccount(userDO.getUserAccount()) != null) {
            throw new RuntimeException("该账号已存在");
        }

        // 2. 密码加密 (非常重要！存入数据库必须是加密后的)
        String rawPassword = userDO.getUserPassword();
        userDO.setUserPassword(securityService.encryptPassword(rawPassword));

        // 3. 设置默认时间
        userDO.setCreateTime(LocalDateTime.now());
        userDO.setUpdateTime(LocalDateTime.now());

        UserDO savedUser = userRepository.save(userDO);
        return convertToDTO(savedUser);
    }

    /**
     * 登录逻辑实现
     */
    @Override
    public UserDTO login(String userAccount, String password) {
        // 1. 根据账号查询用户
        UserDO userDO = userRepository.findByUserAccount(userAccount);
        if (userDO == null) {
            throw new RuntimeException("账号不存在"); // 实际生产建议模糊提示"账号或密码错误"
        }

        // 2. 校验密码 (传入明文和数据库密文进行比对)
        if (!securityService.verifyPassword(password, userDO.getUserPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 验证通过，返回用户信息
        return convertToDTO(userDO);
    }

    @Override
    public String generateToken(UserDTO userDTO) {
        // 将 DTO 转回 DO 或直接传 ID 给 SecurityService 生成 Token
        UserDO userDO = new UserDO();
        userDO.setId(userDTO.getId());
        userDO.setUsername(userDTO.getUsername());
        return securityService.generateToken(userDO);
    }

    // ... 其他 getUserById, getAllUsers 等方法保持不变 ...
    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByPage(UserQuery query) {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDO userDO) {
        UserDO existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existingUser.setUsername(userDO.getUsername());
        existingUser.setPhone(userDO.getPhone());
        existingUser.setUpdateTime(LocalDateTime.now());
        // 如果要更新密码，也需要加密处理，这里暂时略过
        return convertToDTO(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDO.getId());
        userDTO.setUsername(userDO.getUsername());
        userDTO.setEmail(userDO.getUserAccount()); // 暂时映射 Account 到 Email 字段
        userDTO.setCreatedAt(userDO.getCreateTime());
        userDTO.setUpdatedAt(userDO.getUpdateTime());
        return userDTO;
    }
}