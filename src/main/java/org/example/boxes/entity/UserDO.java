package org.example.boxes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_account", nullable = false, length = 255)
    private String userAccount;

    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}