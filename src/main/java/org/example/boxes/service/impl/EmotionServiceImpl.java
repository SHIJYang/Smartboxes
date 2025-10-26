package org.example.boxes.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.EmotionDTO;
import org.example.boxes.dto.QueryEmotionDTO;
import org.example.boxes.entity.EmotionDO;
import org.example.boxes.repository.EmotionRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.EmotionService;
import org.springframework.stereotype.Service;

/**
 * 情感标签服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService {

    private final EmotionRepository emotionRepository;

    /**
     * 添加新的情感标签
     *
     * @param emotionDTO 情感数据传输对象
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> addEmotion(EmotionDTO emotionDTO) {
        // TODO: 在实际应用中应该检查itemId关联的物品是否存在
        // 此处暂略对物品存在的校验

        EmotionDO emotionDO = convertToEntity(emotionDTO);
        emotionDO.setCreateTime(LocalDateTime.now());
        emotionDO.setUpdateTime(LocalDateTime.now());

        try {
            emotionRepository.save(emotionDO);
            log.info("新增情感标签成功，ID: {}", emotionDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("新增情感标签异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 删除情感标签记录
     *
     * @param id 情感记录主键ID
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> deleteEmotion(Long id) {
        Optional<EmotionDO> optionalEmotion = emotionRepository.findById(id);
        if (!optionalEmotion.isPresent()) {
            log.warn("删除情感标签失败：找不到ID为 {} 的情感记录", id);
            return RestResult.error("情感记录不存在");
        }

        try {
            emotionRepository.deleteById(id);
            log.info("删除情感标签成功，ID: {}", id);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("删除情感标签异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新情感标签信息
     *
     * @param emotionDTO 更新的情感数据
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> updateEmotion(EmotionDTO emotionDTO) {
        Optional<EmotionDO> optionalEmotion = emotionRepository.findById(emotionDTO.getId());
        if (!optionalEmotion.isPresent()) {
            log.warn("修改情感标签失败：找不到ID为 {} 的情感记录", emotionDTO.getId());
            return RestResult.error("情感记录不存在");
        }

        EmotionDO emotionDO = optionalEmotion.get();
        if (emotionDTO.getItemId() != null) {
            emotionDO.setItemId(emotionDTO.getItemId());
        }
        if (emotionDTO.getEmotionTag() != null) {
            emotionDO.setEmotionTag(emotionDTO.getEmotionTag());
        }
        if (emotionDTO.getEmotionRemark() != null) {
            emotionDO.setEmotionRemark(emotionDTO.getEmotionRemark());
        }
        emotionDO.setUpdateTime(LocalDateTime.now());

        try {
            emotionRepository.save(emotionDO);
            log.info("修改情感标签成功，ID: {}", emotionDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("修改情感标签异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 查询情感标签列表
     *
     * @param queryEmotionDTO 查询条件参数
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<List<EmotionDTO>> listEmotions(QueryEmotionDTO queryEmotionDTO) {
        // 注意：这里没有使用Specification因为目前只有一项过滤条件
        try {
            List<EmotionDO> emotions;
            if (queryEmotionDTO.getItemId() != null) {
                emotions = emotionRepository.findAllByItemId(queryEmotionDTO.getItemId());
            } else {
                emotions = emotionRepository.findAll();
            }
            List<EmotionDTO> dtos = emotions.stream().map(this::convertToDto).toList();
            log.info("查询情感标签列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询情感标签列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 将DTO转换为DO
     *
     * @param dto DTO对象
     * @return DO对象
     */
    private EmotionDO convertToEntity(EmotionDTO dto) {
        EmotionDO entity = new EmotionDO();
        entity.setId(dto.getId());
        entity.setItemId(dto.getItemId());
        entity.setEmotionTag(dto.getEmotionTag());
        entity.setEmotionRemark(dto.getEmotionRemark());
        return entity;
    }

    /**
     * 将DO转换为DTO
     *
     * @param entity DO对象
     * @return DTO对象
     */
    private EmotionDTO convertToDto(EmotionDO entity) {
        EmotionDTO dto = new EmotionDTO();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setEmotionTag(entity.getEmotionTag());
        dto.setEmotionRemark(entity.getEmotionRemark());
        return dto;
    }
}