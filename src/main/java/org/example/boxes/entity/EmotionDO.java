package org.example.boxes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_emotion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)  // 移除反引号
    private Long itemId;

    @Column(name = "emotion_tag", nullable = false)
    private Integer emotionTag;

    @Column(name = "emotion_remark")
    private String emotionRemark;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    // 暂时注释掉关联关系，先让应用启动
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private ItemDO item;
    */
}