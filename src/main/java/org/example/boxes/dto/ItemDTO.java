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
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String itemName;

    /**
     * 商品描述
     */
    @Size(max = 500, message = "商品描述长度不能超过500个字符")
    private String description;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private Double price;
}