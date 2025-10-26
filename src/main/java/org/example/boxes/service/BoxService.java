
package org.example.boxes.service;

import java.util.List;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;

/**
 * 盒子服务接口
 *
 * @author 14577
 */
public interface BoxService {

    /**
     * 新增盒子
     *
     * @param boxDTO 盒子数据传输对象
     * @return RestResult 结果封装
     */
    RestResult<Void> addBox(BoxDTO boxDTO);

    /**
     * 删除盒子
     *
     * @param id 盒子主键ID
     * @return RestResult 结果封装
     */
    RestResult<Void> deleteBox(Long id);

    /**
     * 修改盒子信息
     *
     * @param boxDTO 更新的盒子数据
     * @return RestResult 结果封装
     */
    RestResult<Void> updateBox(BoxDTO boxDTO);

    /**
     * 查询盒子列表
     *
     * @param queryBoxDTO 查询条件参数
     * @return RestResult 结果封装
     */
    RestResult<List<BoxDTO>> listBoxes(QueryBoxDTO queryBoxDTO);

    /**
     * 分页查询盒子列表
     *
     * @param queryBoxDTO 查询条件参数
     * @param pageQueryDTO 分页条件参数
     * @return RestResult 结果封装
     */
    RestResult<PageResult<BoxDTO>> listBoxesByPage(QueryBoxDTO queryBoxDTO, PageQueryDTO pageQueryDTO);
}
