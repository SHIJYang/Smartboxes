package org.example.boxes.dto;

import lombok.Data;

/**
 * 分页查询条件封装对象
 *
 * @author 14577
 */
@Data
public class PageQueryDTO {

    /**
     * 当前页码，默认为1
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向，asc或desc，默认desc
     */
    private String sortOrder = "desc";
}