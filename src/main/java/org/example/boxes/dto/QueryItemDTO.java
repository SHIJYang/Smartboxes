package org.example.boxes.dto;

import lombok.Data;

/**
 * 物品查询条件封装对象
 *
 * @author 14577
 */
@Data
public class QueryItemDTO {

    /**
     * 用户ID (新增：用于查询该用户下的所有物品)
     */
    private Long userId;

    /**
     * 盒子ID
     */
    private Long boxId;

    /**
     * 物品编码 (支持模糊查询)
     */
    private String itemCode;

    /**
     * 物品名称 (新增：支持搜 自动识别名称 或 手动名称)
     */
    private String name;

    /**
     * 物品标签 (支持模糊查询)
     */
    private String itemTag;

    /**
     * 是否有效
     */
    private Integer isValid;
}