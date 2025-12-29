package org.example.boxes.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.PageQueryDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.entity.BoxDO;
import org.example.boxes.entity.ItemDO;
import org.example.boxes.repository.BoxRepository;
import org.example.boxes.repository.ItemRepository;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 建议添加事务注解

/**
 * 盒子服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository; // 注入物品仓库，用于删除检查

    /**
     * 新增盒子
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> addBox(BoxDTO boxDTO) {
        // 1. 检查编码唯一性
        Optional<BoxDO> existingBox = boxRepository.findByBoxCode(boxDTO.getBoxCode());
        if (existingBox.isPresent()) {
            log.warn("新增盒子失败：盒子编码 {} 已存在", boxDTO.getBoxCode());
            return RestResult.error("盒子编码已存在");
        }

        BoxDO boxDO = convertToEntity(boxDTO);
        boxDO.setId(null); // 确保是新增
        boxDO.setCreateTime(LocalDateTime.now());
        boxDO.setUpdateTime(LocalDateTime.now());
        // 默认心跳时间，防止空指针
        if (boxDO.getLastHeartbeatTime() == null) {
            boxDO.setLastHeartbeatTime(LocalDateTime.now());
        }

        try {
            boxRepository.save(boxDO);
            log.info("新增盒子成功，ID: {}", boxDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("新增盒子异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 删除盒子
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> deleteBox(Long id) {
        Optional<BoxDO> optionalBox = boxRepository.findById(id);
        if (!optionalBox.isPresent()) {
            return RestResult.error("盒子不存在");
        }

        // 2. 关键修复：检查该盒子下是否还有物品
        List<ItemDO> items = itemRepository.findByBoxId(id);
        if (!items.isEmpty()) {
            return RestResult.error("该盒子内仍有 " + items.size() + " 件物品，请先清空或移动物品后再删除");
        }

        try {
            boxRepository.deleteById(id);
            log.info("删除成功，盒子ID: {}", id);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("删除盒子异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 修改盒子信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Void> updateBox(BoxDTO boxDTO) {
        Optional<BoxDO> optionalBox = boxRepository.findById(boxDTO.getId());
        if (!optionalBox.isPresent()) {
            return RestResult.error("盒子信息不存在");
        }

        BoxDO boxDO = optionalBox.get();

        // 3. 关键修复：如果修改了编码，需要检查新编码是否被其他盒子占用
        if (boxDTO.getBoxCode() != null && !boxDTO.getBoxCode().equals(boxDO.getBoxCode())) {
            Optional<BoxDO> duplicateCheck = boxRepository.findByBoxCode(boxDTO.getBoxCode());
            if (duplicateCheck.isPresent() && !duplicateCheck.get().getId().equals(boxDO.getId())) {
                return RestResult.error("新的盒子编码已被占用");
            }
            boxDO.setBoxCode(boxDTO.getBoxCode());
        }

        if (boxDTO.getUserId() != null) boxDO.setUserId(boxDTO.getUserId());
        if (boxDTO.getBoxName() != null) boxDO.setBoxName(boxDTO.getBoxName());
        if (boxDTO.getBoxType() != null) boxDO.setBoxType(boxDTO.getBoxType());
        if (boxDTO.getStatus() != null) boxDO.setStatus(boxDTO.getStatus());
        if (boxDTO.getRssi() != null) boxDO.setRssi(boxDTO.getRssi());
        if (boxDTO.getBattery() != null) boxDO.setBattery(boxDTO.getBattery());
        if (boxDTO.getNetworkDelay() != null) boxDO.setNetworkDelay(boxDTO.getNetworkDelay());
        if (boxDTO.getLastHeartbeatTime() != null) boxDO.setLastHeartbeatTime(boxDTO.getLastHeartbeatTime());
        
        boxDO.setUpdateTime(LocalDateTime.now());

        try {
            boxRepository.save(boxDO);
            log.info("修改盒子成功，ID: {}", boxDO.getId());
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("修改盒子异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 查询盒子列表 (无分页)
     */
    @Override
    public RestResult<List<BoxDTO>> listBoxes(QueryBoxDTO queryBoxDTO) {
        Specification<BoxDO> spec = buildSpecification(queryBoxDTO);
        try {
            List<BoxDO> boxes = boxRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
            List<BoxDTO> dtos = boxes.stream().map(this::convertToDto).toList();
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询盒子列表异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 分页查询盒子列表
     */
    @Override
    public RestResult<PageResult<BoxDTO>> listBoxesByPage(QueryBoxDTO queryBoxDTO, PageQueryDTO pageQueryDTO) {
        Specification<BoxDO> spec = buildSpecification(queryBoxDTO);
        try {
            int page = (pageQueryDTO.getPage() == null || pageQueryDTO.getPage() < 1) ? 0 : pageQueryDTO.getPage() - 1;
            int size = (pageQueryDTO.getSize() == null) ? 10 : pageQueryDTO.getSize();
            String sortOrder = pageQueryDTO.getSortOrder() != null ? pageQueryDTO.getSortOrder() : "DESC";
            String sortField = pageQueryDTO.getSortField() != null ? pageQueryDTO.getSortField() : "createTime";

            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
            Page<BoxDO> boxPage = boxRepository.findAll(spec, PageRequest.of(page, size, sort));
            List<BoxDTO> dtos = boxPage.getContent().stream().map(this::convertToDto).toList();

            PageResult<BoxDTO> pageResult = new PageResult<>(
                    page + 1,
                    size,
                    boxPage.getTotalElements(),
                    dtos
            );
            return RestResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询盒子列表异常", e);
            return RestResult.error("系统错误");
        }
    }

    /**
     * 构建动态查询条件
     */
    private Specification<BoxDO> buildSpecification(QueryBoxDTO queryBoxDTO) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (queryBoxDTO.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), queryBoxDTO.getUserId()));
            }
            if (queryBoxDTO.getBoxCode() != null && !queryBoxDTO.getBoxCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("boxCode"), "%" + queryBoxDTO.getBoxCode() + "%"));
            }
            if (queryBoxDTO.getBoxName() != null && !queryBoxDTO.getBoxName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("boxName"), "%" + queryBoxDTO.getBoxName() + "%"));
            }
            if (queryBoxDTO.getBoxType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("boxType"), queryBoxDTO.getBoxType()));
            }
            if (queryBoxDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryBoxDTO.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private BoxDTO convertToDto(BoxDO boxDO) {
        BoxDTO boxDTO = new BoxDTO();
        boxDTO.setId(boxDO.getId());
        boxDTO.setBoxCode(boxDO.getBoxCode());
        boxDTO.setUserId(boxDO.getUserId());
        boxDTO.setBoxName(boxDO.getBoxName());
        boxDTO.setBoxType(boxDO.getBoxType());
        boxDTO.setStatus(boxDO.getStatus());
        boxDTO.setRssi(boxDO.getRssi());
        boxDTO.setBattery(boxDO.getBattery());
        boxDTO.setNetworkDelay(boxDO.getNetworkDelay());
        boxDTO.setLastHeartbeatTime(boxDO.getLastHeartbeatTime());
        boxDTO.setCreateTime(boxDO.getCreateTime());
        boxDTO.setUpdateTime(boxDO.getUpdateTime());
        return boxDTO;
    }

    private BoxDO convertToEntity(BoxDTO boxDTO) {
        BoxDO boxDO = new BoxDO();
        boxDO.setId(boxDTO.getId());
        boxDO.setBoxCode(boxDTO.getBoxCode());
        boxDO.setUserId(boxDTO.getUserId());
        boxDO.setBoxName(boxDTO.getBoxName());
        boxDO.setBoxType(boxDTO.getBoxType());
        boxDO.setStatus(boxDTO.getStatus());
        boxDO.setRssi(boxDTO.getRssi());
        boxDO.setBattery(boxDTO.getBattery());
        boxDO.setNetworkDelay(boxDTO.getNetworkDelay());
        boxDO.setLastHeartbeatTime(boxDTO.getLastHeartbeatTime());
        boxDO.setCreateTime(boxDTO.getCreateTime());
        boxDO.setUpdateTime(boxDTO.getUpdateTime());
        return boxDO;
    }
}