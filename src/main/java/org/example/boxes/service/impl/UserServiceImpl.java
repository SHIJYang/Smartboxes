package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.User;
import org.example.boxes.query.UserQuery;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 转换为DTO返回
        return convertToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        log.info("根据ID获取用户: {}", id);
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("获取所有用户");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByPage(UserQuery query) {
        log.info("分页查询用户，页面大小: {}, 当前页: {}", query.getPageSize(), query.getCurrentPage());
        // 这里可以添加分页查询的具体实现
        return getAllUsers();
    }

    @Override
    public UserDTO updateUser(Long id, User user) {
        log.info("更新用户: {}", user.getUsername());
        
        // 检查用户是否存在
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新字段
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新后的用户
        User updatedUser = userRepository.save(existingUser);
        
        // 转换为DTO返回
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        userRepository.deleteById(id);
    }

    /**
     * 将User实体转换为UserDTO
     *
     * @param user 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}