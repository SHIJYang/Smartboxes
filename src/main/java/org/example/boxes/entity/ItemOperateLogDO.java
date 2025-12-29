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
@Table(name = "t_item_operate_log")
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
     * 操作类型
     * 1-放入，2-取出，3-手动编辑，4-自动识别更新
     */
    @Column(name = "operate_type", nullable = false)
    private Integer operateType;

    /**
     * 操作用户ID (对应数据库 operate_user_id)
     */
    @Column(name = "operate_user_id", nullable = false)
    private Long operateUserId;

    /**
     * 操作内容 (对应数据库 operate_content)
     */
    @Column(name = "operate_content")
    private String operateContent;

    /**
     * 操作时间
     */
    @Column(name = "operate_time", nullable = false)
    private LocalDateTime operateTime;

    /**
     * 创建时间 (DTO未包含但通常数据库有默认值或需自动填充，此处映射数据库字段)
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 关联的物品（多对一）
     * 外键列名为 item_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemDO item;
}