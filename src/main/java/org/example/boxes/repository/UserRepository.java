package org.example.boxes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.example.boxes.entity.UserDO;
/**
 * 用户仓库接口
 */
@Repository
public interface UserRepository extends JpaRepository<UserDO, Long>, JpaSpecificationExecutor<UserDO> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    UserDO findByUsername(String username);

    // 根据账号查询用户 (用于登录)
    UserDO findByUserAccount(String userAccount);
}