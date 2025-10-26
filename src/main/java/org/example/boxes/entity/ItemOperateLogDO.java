
package org.example.boxes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物品操作日志实体类映射数据库表t_item_operate_log
 *
 * @author 14577
 */
@Entity
@Table(name = "`t_item_operate_log`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOperateLogDO {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物品ID
     */
    @Column(name = "`item_id`", nullable = false)
    private Long itemId;

    /**
     * 操作类型
     */
    @Column(name = "`operate_type`", nullable = false)
    private Integer operateType;

    /**
     * 操作描述
     */
    @Column(name = "`operate_desc`")
    private String operateDesc;

    /**
     * 操作时间
     */
    @Column(name = "`operate_time`", nullable = false)
    private LocalDateTime operateTime;

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
     * 关联的物品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemDO item;
}
