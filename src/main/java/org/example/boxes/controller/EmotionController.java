package org.example.boxes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boxes.dto.EmotionDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryEmotionDTO;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.EmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 情感标签控制器
 *
 * @author 14577
 */
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
@Tag(name = "情感标签管理", description = "情感标签相关API")
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/add")
    @Operation(summary = "添加情感标签")
    public ResponseEntity<RestResult<Void>> addEmotion(@RequestBody @Valid EmotionDTO emotionDTO) {
        return ResponseEntity.ok(emotionService.addEmotion(emotionDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除情感标签")
    public ResponseEntity<RestResult<Void>> deleteEmotion(@PathVariable Long id) {
        return ResponseEntity.ok(emotionService.deleteEmotion(id));
    }

    @PutMapping("/update")
    @Operation(summary = "更新情感标签")
    public ResponseEntity<RestResult<Void>> updateEmotion(@RequestBody @Valid EmotionDTO emotionDTO) {
        return ResponseEntity.ok(emotionService.updateEmotion(emotionDTO));
    }

    @GetMapping("/list")
    @Operation(summary = "查询情感标签列表(无分页)")
    public ResponseEntity<RestResult<List<EmotionDTO>>> listEmotions(QueryEmotionDTO queryEmotionDTO) {
        return ResponseEntity.ok(emotionService.listEmotions(queryEmotionDTO));
    }

    /**
     * 分页查询情感标签
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询情感标签")
    public ResponseEntity<RestResult<PageResult<EmotionDTO>>> listEmotionsByPage(QueryEmotionDTO queryEmotionDTO, PageQueryDTO pageQueryDTO) {
        // 需要在 Service 层实现 listEmotionsByPage
        return ResponseEntity.ok(emotionService.listEmotionsByPage(queryEmotionDTO, pageQueryDTO));
    }
}