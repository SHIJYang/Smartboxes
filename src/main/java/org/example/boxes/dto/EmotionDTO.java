package org.example.boxes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
     * 1-快乐 2-悲伤 3-愤怒 4-焦虑 5-平静
     */
    @NotNull(message = "情感标签不能为空")
    private Integer emotionTag;

    /**
     * 情感备注
     */
    @Size(max = 500, message = "情感备注长度不能超过500个字符")
    private String emotionRemark;
}