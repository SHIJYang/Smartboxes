package org.example.boxes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 商品数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String itemName;
    private String description;
    private Long categoryId;
    private Double price;
}