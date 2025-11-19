package org.example.boxes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false, length = 255)
    private String itemCode;

    @Column(name = "box_id", nullable = false)
    private Long boxId;

    @Column(name = "auto_recognize_name", length = 255)
    private String autoRecognizeName;

    @Column(name = "manual_edit_name", length = 255)
    private String manualEditName;

    @Column(name = "item_tag", length = 255)
    private String itemTag;

    @Column(name = "item_desc", columnDefinition = "TEXT")
    private String itemDesc;

    @Column(name = "put_in_time", nullable = false)
    private LocalDateTime putInTime;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @Column(name = "is_valid", nullable = false)
    private Integer isValid;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", insertable = false, updatable = false)
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

