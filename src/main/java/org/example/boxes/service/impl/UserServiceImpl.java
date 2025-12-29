package org.example.boxes.service.impl;

import jakarta.persistence.criteria.Predicate; // JPA查询构建器
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.repository.UserRepository;
import org.example.boxes.result.PageResult; // 引入 PageResult
import org.example.boxes.result.RestResult;
import org.example.boxes.service.SecurityService;
import org.example.boxes.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            if (userRepository.findByUserAccount(userDO.getUserAccount()) != null) {
                return RestResult.fail("该账号已存在");
            }
            String rawPassword = userDO.getUserPassword();
            userDO.setUserPassword(securityService.encryptPassword(rawPassword));
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
            UserDO userDO = userRepository.findById(id).orElse(null);
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

    /**
     * 实现真正的分页查询
     */
    @Override
    public RestResult<PageResult<UserDTO>> getUsersByPage(UserQuery query) {
        try {
            // 1. 处理页码 (前端传1开始，SpringData需要0开始)
            int page = (query.getCurrentPage() == null || query.getCurrentPage() < 1) ? 0 : query.getCurrentPage() - 1;
            int size = (query.getPageSize() == null || query.getPageSize() < 1) ? 10 : query.getPageSize();

            // 2. 构建动态查询条件
            Specification<UserDO> spec = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                // 如果有关键字，模糊匹配 用户名 OR 账号
                if (StringUtils.hasText(query.getKeyword())) {
                    String likePattern = "%" + query.getKeyword() + "%";
                    Predicate nameLike = cb.like(root.get("username"), likePattern);
                    Predicate accountLike = cb.like(root.get("userAccount"), likePattern);
                    predicates.add(cb.or(nameLike, accountLike));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            // 3. 执行查询 (按创建时间倒序)
            Page<UserDO> pageData = userRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));

            // 4. 转换结果
            List<UserDTO> dtos = pageData.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // 5. 封装 PageResult
            PageResult<UserDTO> result = new PageResult<>(
                    page + 1, // 返回给前端的页码
                    size,
                    pageData.getTotalElements(),
                    dtos
            );

            return RestResult.success(result);
        } catch (Exception e) {
            log.error("分页查询用户失败: ", e);
            return RestResult.fail("分页查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public RestResult<UserDTO> updateUser(Long id, UserDO userDO) {
        try {
            UserDO existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                return RestResult.fail("用户不存在");
            }
            existingUser.setUsername(userDO.getUsername());
            existingUser.setPhone(userDO.getPhone());
            existingUser.setUpdateTime(LocalDateTime.now());

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
            UserDO userDO = userRepository.findByUserAccount(userAccount);
            if (userDO == null) {
                return RestResult.fail("账号或密码错误");
            }
            if (!securityService.verifyPassword(password, userDO.getUserPassword())) {
                return RestResult.fail("账号或密码错误");
            }
            UserDTO userDTO = convertToDTO(userDO);
            String token = generateToken(userDTO);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", userDTO);

            return RestResult.success(result);
        } catch (Exception e) {
            log.error("用户登录失败: ", e);
            return RestResult.fail("登录失败: " + e.getMessage());
        }
    }

    private String generateToken(UserDTO userDTO) {
        UserDO userDO = new UserDO();
        userDO.setId(userDTO.getId());
        userDO.setUsername(userDTO.getUsername());
        return securityService.generateToken(userDO);
    }

    private UserDTO convertToDTO(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDO.getId());
        userDTO.setUsername(userDO.getUsername());
        userDTO.setEmail(userDO.getUserAccount());
        userDTO.setCreatedAt(userDO.getCreateTime());
        userDTO.setUpdatedAt(userDO.getUpdateTime());
        return userDTO;
    }
}