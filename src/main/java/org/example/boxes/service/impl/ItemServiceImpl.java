package org.example.boxes.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.ItemDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryItemDTO;
import org.example.boxes.entity.ItemDO;
import org.example.boxes.repository.BoxRepository;
import org.example.boxes.repository.ItemRepository;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 物品服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BoxRepository boxRepository; // 注入盒子仓库

    /**
     * 添加新物品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> addItem(ItemDTO itemDTO) {
        // 1. 验证盒子是否存在
        if (!boxRepository.existsById(itemDTO.getBoxId())) {
            return RestResult.error("关联的盒子不存在");
        }

        // 2. 验证物品编码是否唯一
        Optional<ItemDO> existingItem = itemRepository.findByItemCode(itemDTO.getItemCode());
        if (existingItem.isPresent()) {
            return RestResult.error("物品编码已存在");
        }

        ItemDO itemDO = convertToEntity(itemDTO);
        itemDO.setId(null);
        itemDO.setCreateTime(LocalDateTime.now());
        itemDO.setUpdateTime(LocalDateTime.now());
        
        // 默认有效
        if (itemDO.getIsValid() == null) {
            itemDO.setIsValid(1);
        }

        try {
            itemRepository.save(itemDO);
            log.info("新增物品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("新增物品异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 删除指定物品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            return RestResult.error("物品不存在");
        }
        try {
            itemRepository.deleteById(id);
            log.info("删除物品成功，ID: {}", id);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("删除物品异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新物品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> updateItem(ItemDTO itemDTO) {
        Optional<ItemDO> optionalItem = itemRepository.findById(itemDTO.getId());
        if (!optionalItem.isPresent()) {
            return RestResult.error("物品信息不存在");
        }

        ItemDO itemDO = optionalItem.get();
        
        // 3. 验证编码唯一性 (如果修改了编码)
        if (itemDTO.getItemCode() != null && !itemDTO.getItemCode().equals(itemDO.getItemCode())) {
            Optional<ItemDO> duplicate = itemRepository.findByItemCode(itemDTO.getItemCode());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(itemDO.getId())) {
                return RestResult.error("新的物品编码已被占用");
            }
            itemDO.setItemCode(itemDTO.getItemCode());
        }

        if (itemDTO.getBoxId() != null) {
             // 如果更换了盒子，检查新盒子是否存在
             if (!boxRepository.existsById(itemDTO.getBoxId())) {
                 return RestResult.error("目标盒子不存在");
             }
             itemDO.setBoxId(itemDTO.getBoxId());
        }

        if (itemDTO.getAutoRecognizeName() != null) itemDO.setAutoRecognizeName(itemDTO.getAutoRecognizeName());
        if (itemDTO.getManualEditName() != null) itemDO.setManualEditName(itemDTO.getManualEditName());
        if (itemDTO.getItemTag() != null) itemDO.setItemTag(itemDTO.getItemTag());
        if (itemDTO.getItemDesc() != null) itemDO.setItemDesc(itemDTO.getItemDesc());
        if (itemDTO.getPutInTime() != null) itemDO.setPutInTime(itemDTO.getPutInTime());
        if (itemDTO.getExpireTime() != null) itemDO.setExpireTime(itemDTO.getExpireTime());
        if (itemDTO.getIsValid() != null) itemDO.setIsValid(itemDTO.getIsValid());
        
        itemDO.setUpdateTime(LocalDateTime.now());

        try {
            itemRepository.save(itemDO);
            log.info("修改物品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("修改物品异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 移动物品到新盒子
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> moveItem(Long itemId, Long targetBoxId) {
        Optional<ItemDO> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return RestResult.error("物品不存在");
        }

        // 4. 验证目标盒子是否存在
        if (!boxRepository.existsById(targetBoxId)) {
            return RestResult.error("目标盒子不存在");
        }

        try {
            ItemDO item = itemOpt.get();
            item.setBoxId(targetBoxId);
            item.setUpdateTime(LocalDateTime.now());
            
            // 这里可以预留：记录 item_operate_log 的逻辑
            
            itemRepository.save(item);
            log.info("物品 {} 已移动到盒子 {}", itemId, targetBoxId);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("移动物品异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新物品状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> updateItemStatus(Long id, Integer isValid) {
        Optional<ItemDO> itemOpt = itemRepository.findById(id);
        if (!itemOpt.isPresent()) {
            return RestResult.error("物品不存在");
        }
        try {
            ItemDO item = itemOpt.get();
            item.setIsValid(isValid);
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("更新物品状态异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 查询物品详细信息
     */
    @Override
    public RestResult<ItemDTO> getItemDetail(Long id) {
        Optional<ItemDO> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            return RestResult.error("物品不存在");
        }
        return RestResult.success(convertToDto(optionalItem.get()));
    }

    /**
     * 综合查询物品列表 (不带分页)
     */
    @Override
    public RestResult<List<ItemDTO>> listItems(QueryItemDTO queryItemDTO) {
        Specification<ItemDO> spec = buildSpecification(queryItemDTO);
        try {
            List<ItemDO> items = itemRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
            List<ItemDTO> dtos = items.stream().map(this::convertToDto).toList();
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询物品列表异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 分页查询物品列表
     */
    @Override
    public RestResult<PageResult<ItemDTO>> listItemsByPage(QueryItemDTO queryItemDTO, PageQueryDTO pageQueryDTO) {
        Specification<ItemDO> spec = buildSpecification(queryItemDTO);
        try {
            int page = (pageQueryDTO.getPage() == null || pageQueryDTO.getPage() < 1) ? 0 : pageQueryDTO.getPage() - 1;
            int size = (pageQueryDTO.getSize() == null) ? 10 : pageQueryDTO.getSize();
            String sortField = StringUtils.hasText(pageQueryDTO.getSortField()) ? pageQueryDTO.getSortField() : "createTime";
            String sortOrder = StringUtils.hasText(pageQueryDTO.getSortOrder()) ? pageQueryDTO.getSortOrder() : "DESC";

            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
            Page<ItemDO> pageData = itemRepository.findAll(spec, PageRequest.of(page, size, sort));
            
            List<ItemDTO> dtos = pageData.getContent().stream().map(this::convertToDto).toList();
            
            PageResult<ItemDTO> pageResult = new PageResult<>(
                page + 1,
                size,
                pageData.getTotalElements(),
                dtos
            );
            return RestResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询物品列表异常", e);
            return RestResult.error("系统错误");
        }
    }

    @Override
    public RestResult<List<ItemDTO>> listItemsByBox(Long boxId) {
        List<ItemDO> items = itemRepository.findByBoxId(boxId);
        return RestResult.success(items.stream().map(this::convertToDto).toList());
    }

    @Override
    public RestResult<List<ItemDTO>> listItemsByUser(Long userId) {
        List<ItemDO> items = itemRepository.findByBox_UserId(userId);
        return RestResult.success(items.stream().map(this::convertToDto).toList());
    }

    /**
     * 构建动态查询条件
     */
    private Specification<ItemDO> buildSpecification(QueryItemDTO query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getBoxId() != null) {
                predicates.add(cb.equal(root.get("boxId"), query.getBoxId()));
            }

            // 5. 跨表查询: Item -> Box -> UserId
            if (query.getUserId() != null) {
                // 利用 JPA Join 关联查询
                predicates.add(cb.equal(root.get("box").get("userId"), query.getUserId()));
            }

            if (StringUtils.hasText(query.getItemCode())) {
                predicates.add(cb.like(root.get("itemCode"), "%" + query.getItemCode() + "%"));
            }

            if (StringUtils.hasText(query.getItemTag())) {
                predicates.add(cb.like(root.get("itemTag"), "%" + query.getItemTag() + "%"));
            }

            if (StringUtils.hasText(query.getName())) {
                Predicate autoName = cb.like(root.get("autoRecognizeName"), "%" + query.getName() + "%");
                Predicate manualName = cb.like(root.get("manualEditName"), "%" + query.getName() + "%");
                predicates.add(cb.or(autoName, manualName));
            }

            if (query.getIsValid() != null) {
                predicates.add(cb.equal(root.get("isValid"), query.getIsValid()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

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