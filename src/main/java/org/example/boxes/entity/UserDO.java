
package org.example.boxes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 *
 * @author 14577
 */
@Data
@Entity
@Table(name = "t_user")
@NoArgsConstructor
@AllArgsConstructor
public class UserDO {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 普通用户登录账号
     */
    @Column(name = "user_account", nullable = false)
    private String userAccount;

    /**
     * 登录密码（加密存储）
     */
    @Column(name = "user_password", nullable = false)
    private String userPassword;

    /**
     * 用户昵称
     */
    @Column(name = "username")
    private String username;

    /**
     * 可选，用于密码找回
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 账号注册时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 信息更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 用户拥有的盒子列表
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BoxDO> boxes;
}
