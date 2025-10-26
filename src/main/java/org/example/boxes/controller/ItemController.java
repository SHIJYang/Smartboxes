
package org.example.boxes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器，负责商品相关操作的API接口
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
@Api(tags = "商品管理")
public class ItemController {

    private final ItemService itemService;

    /**
     * 添加新商品
     *
     * @param itemDTO 商品数据传输对象
     * @return RestResult 结果封装
     */
    @PostMapping("/add")
    @ApiOperation("添加新商品")
    public ResponseEntity<RestResult<Void>> addItem(@RequestBody @Valid ItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.addItem(itemDTO));
    }

    /**
     * 删除指定商品
     *
     * @param id 商品主键ID
     * @return RestResult 结果封装
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除商品")
    public ResponseEntity<RestResult<Void>> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.deleteItem(id));
    }

    /**
     * 更新商品信息
     *
     * @param itemDTO 更新的商品数据
     * @return RestResult 结果封装
     */
    @PutMapping("/update")
    @ApiOperation("更新商品信息")
    public ResponseEntity<RestResult<Void>> updateItem(@RequestBody @Valid ItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.updateItem(itemDTO));
    }

    /**
     * 查询商品列表
     *
     * @param queryItemDTO 查询参数
     * @return RestResult 结果封装
     */
    @GetMapping("/list")
    @ApiOperation("查询商品列表")
    public ResponseEntity<RestResult<List<ItemDTO>>> listItem(QueryItemDTO queryItemDTO) {
        return ResponseEntity.ok(itemService.listItems(queryItemDTO));
    }
}
