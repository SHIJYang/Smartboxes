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
import org.example.boxes.repository.ItemRepository;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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

    /**
     * 添加新物品
     */
    @Override
    public RestResult<Void> addItem(ItemDTO itemDTO) {
        // 验证物品编码是否唯一
        Optional<ItemDO> existingItem = itemRepository.findByItemCode(itemDTO.getItemCode());
        if (existingItem.isPresent()) {
            log.warn("新增物品失败：物品编码 {} 已存在", itemDTO.getItemCode());
            return RestResult.error("物品编码已存在");
        }

        // 插入新物品信息
        ItemDO itemDO = convertToEntity(itemDTO);
        itemDO.setCreateTime(LocalDateTime.now());
        itemDO.setUpdateTime(LocalDateTime.now());

        try {
            itemRepository.save(itemDO);
            log.info("新增物品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("新增物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 删除指定物品
     */
    @Override
    public RestResult<Void> deleteItem(Long id) {
        Optional<ItemDO> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            log.warn("删除物品失败：找不到ID为 {} 的物品信息", id);
            return RestResult.error("物品信息不存在");
        }

        try {
            itemRepository.deleteById(id);
            log.info("删除物品成功，ID: {}", id);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("删除物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新物品信息
     */
    @Override
    public RestResult<Void> updateItem(ItemDTO itemDTO) {
        Optional<ItemDO> optionalItem = itemRepository.findById(itemDTO.getId());
        if (!optionalItem.isPresent()) {
            log.warn("修改物品失败：找不到ID为 {} 的物品信息", itemDTO.getId());
            return RestResult.error("物品信息不存在");
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
            log.info("修改物品成功，ID: {}", itemDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("修改物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 移动物品到新盒子
     */
    @Override
    public RestResult<Void> moveItem(Long itemId, Long targetBoxId) {
        Optional<ItemDO> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return RestResult.error("物品不存在");
        }
        try {
            ItemDO item = itemOpt.get();
            item.setBoxId(targetBoxId);
            item.setUpdateTime(LocalDateTime.now());
            // 可在此处插入日志逻辑到 t_item_operate_log
            
            itemRepository.save(item);
            log.info("物品 {} 已移动到盒子 {}", itemId, targetBoxId);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("移动物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 更新物品状态
     */
    @Override
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
            log.info("物品 {} 状态更新为 {}", id, isValid);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("更新物品状态异常：{}", e.getMessage(), e);
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
            log.warn("查询物品详细信息失败：找不到ID为 {} 的物品信息", id);
            return RestResult.error("物品信息不存在");
        }

        try {
            ItemDO itemDO = optionalItem.get();
            if (itemDO.getBox() != null) {
                log.debug("物品ID: {} 关联盒子: {}", id, itemDO.getBox().getId());
            }
            ItemDTO itemDTO = convertToDto(itemDO);
            log.info("查询物品详细信息成功，ID: {}", id);
            return RestResult.success(itemDTO);
        } catch (Exception e) {
            log.error("查询物品详细信息异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
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
            log.info("查询物品列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询物品列表异常：{}", e.getMessage(), e);
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
            // 处理分页参数，页码从0开始
            int page = Math.max(0, pageQueryDTO.getPage() - 1);
            int size = pageQueryDTO.getSize() != null ? pageQueryDTO.getSize() : 10;
            String sortField = StringUtils.hasText(pageQueryDTO.getSortField()) ? pageQueryDTO.getSortField() : "createTime";
            String sortOrder = StringUtils.hasText(pageQueryDTO.getSortOrder()) ? pageQueryDTO.getSortOrder() : "DESC";

            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
            
            Page<ItemDO> pageData = itemRepository.findAll(spec, PageRequest.of(page, size, sort));
            
            List<ItemDTO> dtos = pageData.getContent().stream().map(this::convertToDto).toList();
            
            PageResult<ItemDTO> pageResult = new PageResult<>(
                page + 1, // 返回给前端的页码习惯从1开始
                size,
                pageData.getTotalElements(),
                dtos
            );
            
            return RestResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询物品列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 根据盒子找物品
     */
    @Override
    public RestResult<List<ItemDTO>> listItemsByBox(Long boxId) {
        try {
            List<ItemDO> items = itemRepository.findByBoxId(boxId);
            List<ItemDTO> dtos = items.stream().map(this::convertToDto).toList();
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("根据盒子查询物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 根据用户找物品
     */
    @Override
    public RestResult<List<ItemDTO>> listItemsByUser(Long userId) {
        try {
            // 需要在 Repository 中定义 findByBox_UserId
            List<ItemDO> items = itemRepository.findByBox_UserId(userId);
            List<ItemDTO> dtos = items.stream().map(this::convertToDto).toList();
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("根据用户查询物品异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 构建动态查询条件 (支持 User, Box, Tag, Name 组合)
     */
    private Specification<ItemDO> buildSpecification(QueryItemDTO query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 根据盒子ID查询
            if (query.getBoxId() != null) {
                predicates.add(cb.equal(root.get("boxId"), query.getBoxId()));
            }

            // 2. 根据用户ID查询 (关联查询：Item -> Box -> User)
            if (query.getUserId() != null) {
                // JPA 会自动处理 join，假设 ItemDO 中有 box 关联关系，BoxDO 中有 userId 字段
                predicates.add(cb.equal(root.get("box").get("userId"), query.getUserId()));
            }

            // 3. 根据物品编码模糊查询
            if (StringUtils.hasText(query.getItemCode())) {
                predicates.add(cb.like(root.get("itemCode"), "%" + query.getItemCode() + "%"));
            }

            // 4. 根据标签模糊查询
            if (StringUtils.hasText(query.getItemTag())) {
                predicates.add(cb.like(root.get("itemTag"), "%" + query.getItemTag() + "%"));
            }

            // 5. 根据名称查询 (同时匹配自动识别名称和手动编辑名称)
            if (StringUtils.hasText(query.getName())) {
                Predicate autoName = cb.like(root.get("autoRecognizeName"), "%" + query.getName() + "%");
                Predicate manualName = cb.like(root.get("manualEditName"), "%" + query.getName() + "%");
                predicates.add(cb.or(autoName, manualName));
            }

            // 6. 根据状态查询
            if (query.getIsValid() != null) {
                predicates.add(cb.equal(root.get("isValid"), query.getIsValid()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 将DTO转换为DO
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