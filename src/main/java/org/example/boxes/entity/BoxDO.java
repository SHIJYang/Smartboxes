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

<<<<<<< HEAD
    @Column(name = "box_type", nullable = false)
    private Integer boxType;  // 确保与数据库的 tinyint 对应

    @Column(name = "status", nullable = false)
=======
    /**
     * 箱体类型 0-子箱，1-主箱
     */
    @Column(name = "`box_type`", nullable = false)
    private Integer boxType;

    /**
     * 状态
     */
    @Column(name = "`status`", nullable = false)
>>>>>>> 274ee59967deb523f16d5cc722ba6731b64355b7
    private Integer status;

    @Column(name = "rssi")
    private Integer rssi;

    @Column(name = "battery")
    private Integer battery;

<<<<<<< HEAD
    @Column(name = "last_heartbeat_time", nullable = false)
=======
    /**
     * 网络时延(ms) 仅主箱记录
     */
    @Column(name = "`network_delay`")
    private Integer networkDelay;

    /**
     * 最后心跳时间
     */
    @Column(name = "`last_heartbeat_time`", nullable = false)
>>>>>>> 274ee59967deb523f16d5cc722ba6731b64355b7
    private LocalDateTime lastHeartbeatTime;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;


}