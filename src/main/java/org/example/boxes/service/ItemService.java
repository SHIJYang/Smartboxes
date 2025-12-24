package org.example.boxes.service;

import java.util.List;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.result.RestResult;

/**
 * 商品服务接口
 *
 * @author 14577
 */
public interface ItemService {

    /**
     * 添加新商品
     *
     * @param itemDTO 商品数据传输对象
     * @return RestResult 结果封装
     */
    RestResult<Void> addItem(ItemDTO itemDTO);

    /**
     * 删除指定商品
     *
     * @param id 商品主键ID
     * @return RestResult 结果封装
     */
    RestResult<Void> deleteItem(Long id);

    /**
     * 更新商品信息
     *
     * @param itemDTO 更新的商品数据
     * @return RestResult 结果封装
     */
    RestResult<Void> updateItem(ItemDTO itemDTO);
    /**
     * 查询商品详细信息
     *
     * @param id 商品主键ID
     * @return RestResult 结果封装
     */
    RestResult<ItemDTO> getItemDetail(Long id);
    /**
     * 查询商品列表
     *
     * @param queryItemDTO 查询参数
     * @return RestResult 结果封装
     */
    RestResult<List<ItemDTO>> listItems(QueryItemDTO queryItemDTO);
}