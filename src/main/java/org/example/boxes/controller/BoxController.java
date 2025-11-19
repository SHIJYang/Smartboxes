package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 盒子控制器，提供盒子的增删改查接口
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
@Tag(name = "盒子管理", description = "盒子相关API")
public class BoxController {

    private final BoxService boxService;

    /**
     * 新增盒子
     */
    @PostMapping("/add")
    @Operation(summary = "新增盒子")
    public ResponseEntity<RestResult<Void>> addBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.addBox(boxDTO));
    }

    /**
     * 删除盒子
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除盒子")
    public ResponseEntity<RestResult<Void>> deleteBox(@PathVariable Long id) {
        return ResponseEntity.ok(boxService.deleteBox(id));
    }

    /**
     * 修改盒子信息
     */
    @PutMapping("/update")
    @Operation(summary = "修改盒子")
    public ResponseEntity<RestResult<Void>> updateBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.updateBox(boxDTO));
    }

    /**
     * 查询盒子列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询盒子列表")
    public ResponseEntity<RestResult<List<BoxDTO>>> listBoxes(QueryBoxDTO queryBoxDTO) {
        return ResponseEntity.ok(boxService.listBoxes(queryBoxDTO));
    }
}