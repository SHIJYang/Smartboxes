package org.example.boxes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_box")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "box_code", nullable = false, length = 255)
    private String boxCode;

    @Column(name = "user_id", nullable = false)  // 修复：移除反引号
    private Long userId;

    @Column(name = "box_name", nullable = false, length = 255)
    private String boxName;

    @Column(name = "box_type", nullable = false)
    private Integer boxType;  // 确保与数据库的 tinyint 对应

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "rssi")
    private Integer rssi;

    @Column(name = "battery")
    private Integer battery;

    @Column(name = "last_heartbeat_time", nullable = false)
    private LocalDateTime lastHeartbeatTime;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;


}