package org.example.boxes.dto;

import lombok.Data;

/**
 * 盒子查询条件封装对象
 *
 * @author 14577
 */
@Data
public class QueryBoxDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 盒子编码
     */
    private String boxCode;

    /**
     * 盒子名称
     */
    private String boxName;

    /**
     * 盒子类型
     */
    private Integer boxType;

    /**
     * 状态
     */
    private Integer status;
}