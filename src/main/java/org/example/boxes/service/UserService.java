package org.example.boxes.service;

import org.example.boxes.dto.UserDTO;
import org.example.boxes.entity.UserDO;
import org.example.boxes.query.UserQuery;
import org.example.boxes.result.PageResult; // 引入 PageResult
import org.example.boxes.result.RestResult;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    RestResult<UserDTO> createUser(UserDO userDO);

    RestResult<UserDTO> getUserById(Long id);

    RestResult<List<UserDTO>> getAllUsers();

    
    RestResult<PageResult<UserDTO>> getUsersByPage(UserQuery query);

    RestResult<UserDTO> updateUser(Long id, UserDO userDO);

    RestResult<Void> deleteUser(Long id);

    RestResult<Map<String, Object>> login(String userAccount, String password);
}