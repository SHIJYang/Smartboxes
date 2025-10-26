package org.example.boxes.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询参数对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery {

    /**
     * 用户名关键词
     */
    private String keyword;

    /**
     * 页面大小
     */
    private Integer pageSize = 10;

    /**
     * 当前页码
     */
    private Integer currentPage = 1;
}