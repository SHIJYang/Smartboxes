package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器，负责商品相关操作的API接口
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品相关API")
public class ItemController {

    private final ItemService itemService;

    /**
     * 添加新商品
     *
     * @param itemDTO 商品数据传输对象
     * @return RestResult 结果封装
     */
    @PostMapping("/add")
    @Operation(summary = "添加新商品", description = "创建新的商品记录")
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
    @Operation(summary = "删除商品", description = "删除指定的商品记录")
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
    @Operation(summary = "更新商品信息", description = "更新商品的基本信息")
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
    @Operation(summary = "查询商品列表", description = "根据条件查询商品列表")
    public ResponseEntity<RestResult<List<ItemDTO>>> listItem(QueryItemDTO queryItemDTO) {
        return ResponseEntity.ok(itemService.listItems(queryItemDTO));
    }
}