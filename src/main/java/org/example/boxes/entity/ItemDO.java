
package org.example.boxes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品实体类映射数据库表t_item
 *
 * @author 14577
 */
@Entity
@Table(name = "`t_item`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDO {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品编码
     */
    @Column(name = "`item_code`", nullable = false, length = 255)
    private String itemCode;

    /**
     * 盒子ID
     */
    @Column(name = "`box_id`", nullable = false)
    private Long boxId;

    /**
     * 自动识别名称
     */
    @Column(name = "`auto_recognize_name`", length = 255)
    private String autoRecognizeName;

    /**
     * 手动编辑名称
     */
    @Column(name = "`manual_edit_name`", length = 255)
    private String manualEditName;

    /**
     * 商品标签
     */
    @Column(name = "`item_tag`", length = 255)
    private String itemTag;

    /**
     * 商品描述
     */
    @Column(name = "`item_desc`")
    private String itemDesc;

    /**
     * 放入时间
     */
    @Column(name = "`put_in_time`", nullable = false)
    private LocalDateTime putInTime;

    /**
     * 过期时间
     */
    @Column(name = "`expire_time`")
    private LocalDateTime expireTime;

    /**
     * 是否有效
     */
    @Column(name = "`is_valid`", nullable = false)
    private Integer isValid;

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
     * 所属盒子
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id")
    private BoxDO box;

    /**
     * 物品的情感标签（由 EmotionDO.item 维护外键）
     */
    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private EmotionDO emotion;

    /**
     * 物品的操作日志列表
     */
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<ItemOperateLogDO> operateLogs;
}

