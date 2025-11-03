
package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.EmotionDTO;
import org.example.boxes.dto.QueryEmotionDTO;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.EmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 情感标签控制器，处理情感相关的CRUD操作
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
@Tag(name = "情感标签管理")
public class EmotionController {

    private final EmotionService emotionService;

    /**
     * 添加新的情感标签
     *
     * @param emotionDTO 情感数据传输对象
     * @return RestResult 结果封装
     */
    @PostMapping("")
    @Operation(summary = "添加情感标签")
    public ResponseEntity<RestResult<Void>> addEmotion(@RequestBody @Valid EmotionDTO emotionDTO) {
        return ResponseEntity.ok(emotionService.addEmotion(emotionDTO));
    }

    /**
     * 删除情感标签记录
     *
     * @param id 情感记录主键ID
     * @return RestResult 结果封装
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除情感标签")
    public ResponseEntity<RestResult<Void>> deleteEmotion(@PathVariable Long id) {
        return ResponseEntity.ok(emotionService.deleteEmotion(id));
    }

    /**
     * 更新情感标签信息
     *
     * @param emotionDTO 更新的情感数据
     * @return RestResult 结果封装
     */
    @PutMapping("")
    @Operation(summary = "更新情感标签")
    public ResponseEntity<RestResult<Void>> updateEmotion(@RequestBody @Valid EmotionDTO emotionDTO) {
        return ResponseEntity.ok(emotionService.updateEmotion(emotionDTO));
    }

    /**
     * 查询情感标签列表
     *
     * @param queryEmotionDTO 查询条件参数
     * @return RestResult 结果封装
     */
    @GetMapping("")
    @Operation(summary = "查询情感标签列表")
    public ResponseEntity<RestResult<List<EmotionDTO>>> listEmotions(QueryEmotionDTO queryEmotionDTO) {
        return ResponseEntity.ok(emotionService.listEmotions(queryEmotionDTO));
    }
}
