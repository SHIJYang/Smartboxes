
package org.example.boxes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxes.entity.ItemDO;
import org.example.boxes.entity.UserDO;

import java.time.LocalDateTime;

/**
 * 盒子实体类映射数据库表t_box
 *
 * @author 14577
 */
@Entity
@Table(name = "`t_box`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxDO {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 盒子编码
     */
    @Column(name = "`box_code`", nullable = false, length = 255)
    private String boxCode;

    /**
     * 用户ID
     */
    @Column(name = "`user_id`", nullable = false)
    private Long userId;

    /**
     * 盒子名称
     */
    @Column(name = "`box_name`", nullable = false, length = 255)
    private String boxName;

    /**
     * 箱体类型 0-子箱，1-主箱
     */
    @Column(name = "`box_type`", nullable = false)
    private Integer boxType;

    /**
     * 状态
     */
    @Column(name = "`status`", nullable = false)
    private Integer status;

    /**
     * 信号强度
     */
    @Column(name = "`rssi`")
    private Integer rssi;

    /**
     * 电池电量
     */
    @Column(name = "`battery`")
    private Integer battery;

    /**
     * 网络时延(ms) 仅主箱记录
     */
    @Column(name = "`network_delay`")
    private Integer networkDelay;

    /**
     * 最后心跳时间
     */
    @Column(name = "`last_heartbeat_time`", nullable = false)
    private LocalDateTime lastHeartbeatTime;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`", nullable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "`update_time`", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 所属用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserDO user;

    /**
     * 盒子中的物品列表
     */
    @OneToMany(mappedBy = "box", fetch = FetchType.LAZY)
    private java.util.List<ItemDO> items;
}

