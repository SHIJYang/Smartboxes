package org.example.boxes.service;

import java.util.List;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;

/**
 * 物品服务接口
 *
 * @author 14577
 */
public interface ItemService {

    RestResult<Void> addItem(ItemDTO itemDTO);
    RestResult<Void> deleteItem(Long id);
    RestResult<Void> updateItem(ItemDTO itemDTO);
    RestResult<ItemDTO> getItemDetail(Long id);

    /**
     * 移动物品到另一个盒子
     */
    RestResult<Void> moveItem(Long itemId, Long targetBoxId);

    /**
     * 更新物品状态
     */
    RestResult<Void> updateItemStatus(Long id, Integer isValid);

    /**
     * 综合查询物品列表 (支持 User, Box, Tag, Code 组合)
     */
    RestResult<List<ItemDTO>> listItems(QueryItemDTO queryItemDTO);

    /**
     * 分页查询物品列表 (支持 User, Box, Tag, Code 组合)
     */
    RestResult<PageResult<ItemDTO>> listItemsByPage(QueryItemDTO queryItemDTO, PageQueryDTO pageQueryDTO);

    /**
     * 快捷方法：根据盒子ID查找所有物品
     */
    RestResult<List<ItemDTO>> listItemsByBox(Long boxId);

    /**
     * 快捷方法：根据用户ID查找所有物品
     */
    RestResult<List<ItemDTO>> listItemsByUser(Long userId);
}