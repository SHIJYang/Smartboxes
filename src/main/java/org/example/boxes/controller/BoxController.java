
package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 盒子控制器，提供盒子的增删改查接口
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
@Tag(name = "盒子管理")
public class BoxController {

    private final BoxService boxService;

    /**
     * 新增盒子
     *
     * @param boxDTO 盒子数据传输对象
     * @return RestResult 结果封装
     * @apiNote 用于添加新的收纳盒信息
     */
    @PostMapping("/add")
    @Operation(summary = "新增盒子")
    public ResponseEntity<RestResult<Void>> addBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.addBox(boxDTO));
    }

    /**
     * 删除盒子
     *
     * @param id 盒子ID
     * @return RestResult 结果封装
     * @apiNote 通过ID删除指定的收纳盒
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除盒子")
    public ResponseEntity<RestResult<Void>> deleteBox(@PathVariable Long id) {
        return ResponseEntity.ok(boxService.deleteBox(id));
    }

    /**
     * 修改盒子信息
     *
     * @param boxDTO 更新的盒子数据
     * @return RestResult 结果封装
     * @apiNote 更新盒子的基本信息
     */
    @PutMapping("")
    @ApiOperation("修改盒子")
    public ResponseEntity<RestResult<Void>> updateBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.updateBox(boxDTO));
    }

    /**
     * 查询盒子列表
     *
     * @param queryBoxDTO 查询条件参数
     * @return RestResult 结果封装
     * @apiNote 支持按用户ID、状态等条件查询盒子列表
     */
    @GetMapping("")
    @Operation(summary = "查询盒子列表")
    public ResponseEntity<RestResult<List<BoxDTO>>> listBoxes(QueryBoxDTO queryBoxDTO) {
        return ResponseEntity.ok(boxService.listBoxes(queryBoxDTO));
    }
}
