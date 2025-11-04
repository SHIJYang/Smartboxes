package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 商品数据传输对象
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
     * 商品编码
     */
    @NotBlank(message = "商品编码不能为空")
    @Size(max = 255, message = "商品编码长度不能超过255个字符")
    private String itemCode;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String itemName;

    /**
     * 商品描述 - 对应数据库的 item_desc 字段
     */
    @Size(max = 500, message = "商品描述长度不能超过500个字符")
    private String itemDesc;

    /**
     * 盒子ID
     */
    @NotNull(message = "盒子ID不能为空")
    private Long boxId;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private Double price;

    // 其他字段
    private String autoRecognizeName;
    private String manualEditName;
    private String itemTag;
    private java.time.LocalDateTime putInTime;
    private java.time.LocalDateTime expireTime;
    private Integer isValid;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;
}