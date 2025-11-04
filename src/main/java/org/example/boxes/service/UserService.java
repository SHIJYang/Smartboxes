package org.example.boxes.service;

import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param userDO 用户实体
     * @return 用户DTO
     */
    UserDTO createUser(UserDO userDO);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    Optional<UserDTO> getUserById(Long id);

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 分页查询用户
     *
     * @param query 查询参数
     * @return 用户列表
     */
    List<UserDTO> getUsersByPage(UserQuery query);

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param userDO 用户实体
     * @return 用户DTO
     */
    UserDTO updateUser(Long id, UserDO userDO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
}