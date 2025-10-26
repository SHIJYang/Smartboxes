package org.example.boxes.dto;

import lombok.Data;

/**
 * 商品查询条件封装对象
 *
 * @author 14577
 */
@Data
public class QueryItemDTO {

    /**
     * 盒子ID
     */
    private Long boxId;

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 商品标签
     */
    private String itemTag;

    /**
     * 是否有效
     */
    private Integer isValid;
}