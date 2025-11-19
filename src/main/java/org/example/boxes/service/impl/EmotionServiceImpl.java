package org.example.boxes.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.EmotionDTO;
import org.example.boxes.dto.QueryEmotionDTO;
import org.example.boxes.entity.EmotionDO;
import org.example.boxes.entity.ItemDO;
import org.example.boxes.repository.EmotionRepository;
import org.example.boxes.repository.ItemRepository; // 需要注入 ItemRepository 用于校验和关联
import org.example.boxes.result.RestResult;
import org.example.boxes.service.EmotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ItemRepository itemRepository; // 新增依赖：用于校验 itemId 是否存在

    /**
     * 添加新的情感标签
     *
     * @param emotionDTO 情感数据传输对象
     * @return RestResult 结果封装
     */
    @Override
    @Transactional
    public RestResult<Void> addEmotion(EmotionDTO emotionDTO) {
        if (emotionDTO.getItemId() == null) {
            log.warn("新增情感标签失败：itemId 不能为空");
            return RestResult.error("物品ID不能为空");
        }

        // 校验物品是否存在
        Optional<ItemDO> itemOpt = itemRepository.findById(emotionDTO.getItemId());
        if (itemOpt.isEmpty()) {
            log.warn("新增情感标签失败：物品ID {} 不存在", emotionDTO.getItemId());
            return RestResult.error("关联的物品不存在");
        }

        EmotionDO emotionDO = convertToEntity(emotionDTO, itemOpt.get());
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
    @Transactional
    public RestResult<Void> deleteEmotion(Long id) {
        if (!emotionRepository.existsById(id)) {
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
    @Transactional
    public RestResult<Void> updateEmotion(EmotionDTO emotionDTO) {
        if (emotionDTO.getId() == null) {
            return RestResult.error("情感记录ID不能为空");
        }

        Optional<EmotionDO> optionalEmotion = emotionRepository.findById(emotionDTO.getId());
        if (optionalEmotion.isEmpty()) {
            log.warn("修改情感标签失败：找不到ID为 {} 的情感记录", emotionDTO.getId());
            return RestResult.error("情感记录不存在");
        }

        EmotionDO emotionDO = optionalEmotion.get();

        // 如果更新了 itemId，则需重新关联 ItemDO
        if (emotionDTO.getItemId() != null && !emotionDTO.getItemId().equals(emotionDO.getItem().getId())) {
            Optional<ItemDO> newItemOpt = itemRepository.findById(emotionDTO.getItemId());
            if (newItemOpt.isEmpty()) {
                log.warn("修改情感标签失败：物品ID {} 不存在", emotionDTO.getItemId());
                return RestResult.error("关联的物品不存在");
            }
            emotionDO.setItem(newItemOpt.get());
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
        try {
            List<EmotionDO> emotions;
            if (queryEmotionDTO.getItemId() != null) {
                emotions = emotionRepository.findAllByItemId(queryEmotionDTO.getItemId());
            } else {
                emotions = emotionRepository.findAll();
            }
            List<EmotionDTO> dtos = emotions.stream()
                    .map(this::convertToDto)
                    .toList();
            log.info("查询情感标签列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询情感标签列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 将DTO转换为DO（注意：itemId 通过 ItemDO 关联）
     */
    private EmotionDO convertToEntity(EmotionDTO dto, ItemDO item) {
        EmotionDO entity = new EmotionDO();
        entity.setId(dto.getId());
        entity.setItem(item); // 关键：设置关联对象，而非 itemId
        entity.setEmotionTag(dto.getEmotionTag());
        entity.setEmotionRemark(dto.getEmotionRemark());
        return entity;
    }

    /**
     * 将DO转换为DTO（从 item.getId() 获取 itemId）
     */
    private EmotionDTO convertToDto(EmotionDO entity) {
        EmotionDTO dto = new EmotionDTO();
        dto.setId(entity.getId());
        // 从关联对象获取 itemId（注意判空）
        dto.setItemId(entity.getItem() != null ? entity.getItem().getId() : null);
        dto.setEmotionTag(entity.getEmotionTag());
        dto.setEmotionRemark(entity.getEmotionRemark());
        return dto;
    }
}