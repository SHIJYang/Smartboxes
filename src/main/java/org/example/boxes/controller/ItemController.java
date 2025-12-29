package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物品控制器，负责物品相关操作的API接口
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "物品管理", description = "提供按用户、按盒子及综合搜索物品的功能")
public class ItemController {

    private final ItemService itemService;

    /**
     * 添加新物品
     */
    @PostMapping("/add")
    @Operation(summary = "添加新物品", description = "创建新的物品记录")
    public ResponseEntity<RestResult<Void>> addItem(@RequestBody @Valid ItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.addItem(itemDTO));
    }

    /**
     * 删除指定物品
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除物品", description = "删除指定的物品记录")
    public ResponseEntity<RestResult<Void>> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.deleteItem(id));
    }

    /**
     * 更新物品信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新物品信息", description = "更新物品的基本信息")
    public ResponseEntity<RestResult<Void>> updateItem(@RequestBody @Valid ItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.updateItem(itemDTO));
    }

    /**
     * 移动物品到另一个盒子
     */
    @PutMapping("/move/{itemId}/to/{boxId}")
    @Operation(summary = "移动物品", description = "将物品移动到指定的盒子中")
    public ResponseEntity<RestResult<Void>> moveItem(@PathVariable Long itemId, @PathVariable Long boxId) {
        return ResponseEntity.ok(itemService.moveItem(itemId, boxId));
    }

    /**
     * 快速更新物品状态 (例如：取出/放入)
     */
    @PutMapping("/status/{id}/{isValid}")
    @Operation(summary = "更新物品状态", description = "设置物品为有效(1)或无效(0)")
    public ResponseEntity<RestResult<Void>> updateItemStatus(@PathVariable Long id, @PathVariable Integer isValid) {
        return ResponseEntity.ok(itemService.updateItemStatus(id, isValid));
    }

    /**
     * 查询物品详细信息
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "查询物品详细信息", description = "查询指定物品的详细信息，包含关联数据")
    public ResponseEntity<RestResult<ItemDTO>> getItemDetail(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemDetail(id));
    }

    /**
     * 查询物品列表 (不带分页，可用于少量数据的筛选)
     */
    @GetMapping("/list")
    @Operation(summary = "查询物品列表", description = "根据条件查询物品列表")
    public ResponseEntity<RestResult<List<ItemDTO>>> listItems(QueryItemDTO queryItemDTO) {
        return ResponseEntity.ok(itemService.listItems(queryItemDTO));
    }

    /**
     * 快捷：根据盒子ID查询物品
     */
    @GetMapping("/by-box/{boxId}")
    @Operation(summary = "根据盒子找物品", description = "获取指定盒子内的所有物品")
    public ResponseEntity<RestResult<List<ItemDTO>>> listItemsByBox(@PathVariable Long boxId) {
        return ResponseEntity.ok(itemService.listItemsByBox(boxId));
    }

    /**
     * 快捷：根据用户ID查询物品
     */
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "根据用户找物品", description = "获取指定用户的所有物品（跨所有盒子）")
    public ResponseEntity<RestResult<List<ItemDTO>>> listItemsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(itemService.listItemsByUser(userId));
    }

    /**
     * 综合分页查询 (支持 userId, boxId, name 等组合筛选)
     */
    @GetMapping("/page")
    @Operation(summary = "综合分页查询", description = "支持按用户、盒子、名称、标签等组合筛选物品")
    public ResponseEntity<RestResult<PageResult<ItemDTO>>> listItemsByPage(QueryItemDTO queryItemDTO, PageQueryDTO pageQueryDTO) {
        return ResponseEntity.ok(itemService.listItemsByPage(queryItemDTO, pageQueryDTO));
    }
}