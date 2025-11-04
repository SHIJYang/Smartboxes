package org.example.boxes.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDO userDO) {
        UserDO savedUser = userRepository.save(userDO);
        return convertToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByPage(UserQuery query) {
        // 实现分页逻辑
        // 这里需要根据您的分页需求实现具体逻辑
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDO userDO) {
        UserDO existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新字段
        existingUser.setUserAccount(userDO.getUserAccount());
        existingUser.setUsername(userDO.getUsername());
        existingUser.setPhone(userDO.getPhone());
        // 注意：密码更新需要特殊处理，通常需要加密

        UserDO updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDO.getId());
        userDTO.setUsername(userDO.getUsername());
        // 注意：这里可能需要根据您的 UserDTO 字段进行调整
        // 如果 UserDTO 没有 email 字段，可以注释掉或设置为 null
        // userDTO.setEmail(userDO.getUserAccount()); // 如果需要，可以将 userAccount 作为 email
        userDTO.setCreatedAt(userDO.getCreateTime());
        userDTO.setUpdatedAt(userDO.getUpdateTime());
        return userDTO;
    }
}