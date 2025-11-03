
package org.example.boxes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 情感标签实体类映射数据库表t_emotion
 *
 * @author 14577
 */
@Entity
@Table(name = "`t_emotion`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDO {

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
     * 情感标签
     */
    @Column(name = "`emotion_tag`", nullable = false)
    private Integer emotionTag;

    /**
     * 情感备注
     */
    @Column(name = "`emotion_remark`")
    private String emotionRemark;

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
     * 关联的物品（一对一，外键在 t_emotion.item_id）
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ItemDO item;
}
