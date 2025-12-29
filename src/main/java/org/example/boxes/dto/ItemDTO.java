package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 物品数据传输对象
 *
 * @author 14577
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 物品编码
     */
    @NotBlank(message = "物品编码不能为空")
    @Size(max = 255, message = "物品编码长度不能超过255个字符")
    private String itemCode;

    /**
     * 盒子ID
     */
    @NotNull(message = "盒子ID不能为空")
    private Long boxId;

    /**
     * 自动识别名称
     */
    private String autoRecognizeName;

    /**
     * 手动编辑名称
     */
    private String manualEditName;

    /**
     * 物品标签
     */
    private String itemTag;

    /**
     * 物品描述 - 对应数据库的 item_desc 字段
     */
    @Size(max = 500, message = "物品描述长度不能超过500个字符")
    private String itemDesc;

    /**
     * 放入时间
     */
    private java.time.LocalDateTime putInTime;

    /**
     * 过期时间
     */
    private java.time.LocalDateTime expireTime;

    /**
     * 是否有效（0-已取出，1-在盒内）
     */
    private Integer isValid;
}