package org.example.boxes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 情感标签数据传输对象
 *
 * @author 14577
 */
@Data
public class EmotionDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 物品ID（必填）
     */
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    /**
     * 情感标签（必填）
     */
    @NotNull(message = "情感标签不能为空")
    private Integer emotionTag;

    /**
     * 情感备注
     */
    private String emotionRemark;
}