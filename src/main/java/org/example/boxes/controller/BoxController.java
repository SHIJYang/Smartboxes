package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 盒子控制器
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
@Tag(name = "盒子管理", description = "盒子相关API")
public class BoxController {

    private final BoxService boxService;

    @PostMapping("/add")
    @Operation(summary = "新增盒子")
    public ResponseEntity<RestResult<Void>> addBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.addBox(boxDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除盒子")
    public ResponseEntity<RestResult<Void>> deleteBox(@PathVariable Long id) {
        return ResponseEntity.ok(boxService.deleteBox(id));
    }

    @PutMapping("/update")
    @Operation(summary = "修改盒子信息")
    public ResponseEntity<RestResult<Void>> updateBox(@RequestBody @Valid BoxDTO boxDTO) {
        return ResponseEntity.ok(boxService.updateBox(boxDTO));
    }

    /**
     * 上报盒子状态 (心跳/电量/RSSI)
     * 这是一个专门用于硬件或定时任务调用的接口，比通用 update 更具体
     */
    @PutMapping("/report/status")
    @Operation(summary = "上报盒子状态", description = "更新盒子的在线状态、电量和信号强度")
    public ResponseEntity<RestResult<Void>> reportBoxStatus(@RequestBody @Valid BoxDTO boxDTO) {
        // 这里可以复用 updateBox 或在 Service 建立专门的 reportStatus 方法以处理心跳逻辑
        return ResponseEntity.ok(boxService.updateBox(boxDTO));
    }

    @GetMapping("/list")
    @Operation(summary = "查询盒子列表(无分页)")
    public ResponseEntity<RestResult<List<BoxDTO>>> listBoxes(QueryBoxDTO queryBoxDTO) {
        return ResponseEntity.ok(boxService.listBoxes(queryBoxDTO));
    }

    /**
     * 分页查询盒子
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询盒子", description = "支持筛选条件的分页查询")
    public ResponseEntity<RestResult<PageResult<BoxDTO>>> listBoxesByPage(QueryBoxDTO queryBoxDTO, PageQueryDTO pageQueryDTO) {
        return ResponseEntity.ok(boxService.listBoxesByPage(queryBoxDTO, pageQueryDTO));
    }
}