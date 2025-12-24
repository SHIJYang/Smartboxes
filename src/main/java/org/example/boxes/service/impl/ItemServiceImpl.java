package org.example.boxes.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.entity.ItemDO;
import org.example.boxes.repository.ItemRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    /**
     * 添加新商品
     *
     * @param itemDTO 商品数据传输对象
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> addItem(ItemDTO itemDTO) {
        // 验证商品编码是否唯一
        Optional<ItemDO> existingItem = itemRepository.findByItemCode(itemDTO.getItemCode());
        if (existingItem.isPresent()) {
            log.warn("新增商品失败：商品编码 {} 已存在", itemDTO.getItemCode());
            return RestResult.error("商品编码已存在");
        }

        // 插入新商品信息
        ItemDO itemDO = convertToEntity(itemDTO);
        itemDO.setCreateTime(LocalDateTime.now());
        itemDO.setUpdateTime(LocalDateTime.now());

        try {
            itemRepository.save(itemDO);
            log.info("新增商品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("新增商品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 删除指定商品
     *
     * @param id 商品主键ID
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> deleteItem(Long id) {
        Optional<ItemDO> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            log.warn("删除商品失败：找不到ID为 {} 的商品信息", id);
            return RestResult.error("商品信息不存在");
        }

        try {
            itemRepository.deleteById(id);
            log.info("删除商品成功，ID: {}", id);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("删除商品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新商品信息
     *
     * @param itemDTO 更新的商品数据
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> updateItem(ItemDTO itemDTO) {
        Optional<ItemDO> optionalItem = itemRepository.findById(itemDTO.getId());
        if (!optionalItem.isPresent()) {
            log.warn("修改商品失败：找不到ID为 {} 的商品信息", itemDTO.getId());
            return RestResult.error("商品信息不存在");
        }

        ItemDO itemDO = optionalItem.get();
        // 只更新传入了值的字段
        if (itemDTO.getItemCode() != null) {
            itemDO.setItemCode(itemDTO.getItemCode());
        }
        if (itemDTO.getBoxId() != null) {
            itemDO.setBoxId(itemDTO.getBoxId());
        }
        if (itemDTO.getAutoRecognizeName() != null) {
            itemDO.setAutoRecognizeName(itemDTO.getAutoRecognizeName());
        }
        if (itemDTO.getManualEditName() != null) {
            itemDO.setManualEditName(itemDTO.getManualEditName());
        }
        if (itemDTO.getItemTag() != null) {
            itemDO.setItemTag(itemDTO.getItemTag());
        }
        if (itemDTO.getItemDesc() != null) {
            itemDO.setItemDesc(itemDTO.getItemDesc());
        }
        if (itemDTO.getPutInTime() != null) {
            itemDO.setPutInTime(itemDTO.getPutInTime());
        }
        if (itemDTO.getExpireTime() != null) {
            itemDO.setExpireTime(itemDTO.getExpireTime());
        }
        if (itemDTO.getIsValid() != null) {
            itemDO.setIsValid(itemDTO.getIsValid());
        }
        itemDO.setUpdateTime(LocalDateTime.now());

        try {
            itemRepository.save(itemDO);
            log.info("修改商品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("修改商品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 查询商品列表
     *
     * @param queryItemDTO 查询参数
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<List<ItemDTO>> listItems(QueryItemDTO queryItemDTO) {
        Specification<ItemDO> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (queryItemDTO.getBoxId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("boxId"), queryItemDTO.getBoxId()));
            }
            if (queryItemDTO.getItemCode() != null && !queryItemDTO.getItemCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("itemCode"), "%" + queryItemDTO.getItemCode() + "%"));
            }
            if (queryItemDTO.getItemTag() != null && !queryItemDTO.getItemTag().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("itemTag"), "%" + queryItemDTO.getItemTag() + "%"));
            }
            if (queryItemDTO.getIsValid() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isValid"), queryItemDTO.getIsValid()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        try {
            List<ItemDO> items = itemRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
            List<ItemDTO> dtos = items.stream().map(this::convertToDto).toList();
            log.info("查询商品列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询商品列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 查询商品详细信息
     *
     * @param id 商品主键ID
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<ItemDTO> getItemDetail(Long id) {
        Optional<ItemDO> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            log.warn("查询商品详细信息失败：找不到ID为 {} 的商品信息", id);
            return RestResult.error("商品信息不存在");
        }

        try {
            ItemDO itemDO = optionalItem.get();

            // 如果需要关联查询，可以在这里处理
            // 例如：如果需要查询关联的盒子信息
            if (itemDO.getBox() != null) {
                log.debug("商品ID: {} 关联盒子: {}", id, itemDO.getBox().getId());
            }

            // 转换DTO时，可以添加额外的逻辑
            ItemDTO itemDTO = convertToDto(itemDO);
            log.info("查询商品详细信息成功，ID: {}", id);
            return RestResult.success(itemDTO);
        } catch (Exception e) {
            log.error("查询商品详细信息异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 将DTO转换为DO
     *
     * @param dto DTO对象
     * @return DO对象
     */
    private ItemDO convertToEntity(ItemDTO dto) {
        ItemDO entity = new ItemDO();
        entity.setId(dto.getId());
        entity.setItemCode(dto.getItemCode());
        entity.setBoxId(dto.getBoxId());
        entity.setAutoRecognizeName(dto.getAutoRecognizeName());
        entity.setManualEditName(dto.getManualEditName());
        entity.setItemTag(dto.getItemTag());
        entity.setItemDesc(dto.getItemDesc());
        entity.setPutInTime(dto.getPutInTime());
        entity.setExpireTime(dto.getExpireTime());
        entity.setIsValid(dto.getIsValid());
        return entity;
    }

    /**
     * 将DO转换为DTO
     *
     * @param entity DO对象
     * @return DTO对象
     */
    private ItemDTO convertToDto(ItemDO entity) {
        ItemDTO dto = new ItemDTO();
        dto.setId(entity.getId());
        dto.setItemCode(entity.getItemCode());
        dto.setBoxId(entity.getBoxId());
        dto.setAutoRecognizeName(entity.getAutoRecognizeName());
        dto.setManualEditName(entity.getManualEditName());
        dto.setItemTag(entity.getItemTag());
        dto.setItemDesc(entity.getItemDesc());
        dto.setPutInTime(entity.getPutInTime());
        dto.setExpireTime(entity.getExpireTime());
        dto.setIsValid(entity.getIsValid());
        return dto;
    }
}