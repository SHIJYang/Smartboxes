
package org.example.boxes.service.impl;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxes.dto.BoxDTO;
import org.example.boxes.dto.QueryBoxDTO;
import org.example.boxes.entity.BoxDO;
import org.example.boxes.repository.BoxRepository;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 盒子服务实现类
 *
 * @author 14577
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;

    /**
     * 新增盒子
     *
     * @param boxDTO 盒子数据传输对象
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> addBox(BoxDTO boxDTO) {
        // 校验传入的盒子编码是否已存在
        Optional<BoxDO> existingBox = boxRepository.findByBoxCode(boxDTO.getBoxCode());
        if (existingBox.isPresent()) {
            log.warn("新增盒子失败：盒子编码 {} 已存在", boxDTO.getBoxCode());
            return RestResult.error("盒子编码已存在");
        }

        // 将新盒子信息保存到数据库
        BoxDO boxDO = convertToEntity(boxDTO);
        boxDO.setCreateTime(LocalDateTime.now());
        boxDO.setUpdateTime(LocalDateTime.now());

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
     *
     * @param id 盒子主键ID
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> deleteBox(Long id) {
        Optional<BoxDO> optionalBox = boxRepository.findById(id);
        if (!optionalBox.isPresent()) {
            log.warn("删除盒子失败：找不到ID为 {} 的盒子信息", id);
            return RestResult.error("盒子信息不存在");
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
     *
     * @param boxDTO 更新的盒子数据
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<Void> updateBox(BoxDTO boxDTO) {
        Optional<BoxDO> optionalBox = boxRepository.findById(boxDTO.getId());
        if (!optionalBox.isPresent()) {
            log.warn("修改盒子失败：找不到ID为 {} 的盒子信息", boxDTO.getId());
            return RestResult.error("盒子信息不存在");
        }

        BoxDO boxDO = optionalBox.get();
        // 只更新传入了值的字段
        if (boxDTO.getBoxCode() != null) {
            boxDO.setBoxCode(boxDTO.getBoxCode());
        }
        if (boxDTO.getUserId() != null) {
            boxDO.setUserId(boxDTO.getUserId());
        }
        if (boxDTO.getBoxName() != null) {
            boxDO.setBoxName(boxDTO.getBoxName());
        }
        if (boxDTO.getBoxType() != null) {
            boxDO.setBoxType(boxDTO.getBoxType());
        }
        if (boxDTO.getStatus() != null) {
            boxDO.setStatus(boxDTO.getStatus());
        }
        if (boxDTO.getRssi() != null) {
            boxDO.setRssi(boxDTO.getRssi());
        }
        if (boxDTO.getBattery() != null) {
            boxDO.setBattery(boxDTO.getBattery());
        }
        if (boxDTO.getNetworkDelay() != null) {
            boxDO.setNetworkDelay(boxDTO.getNetworkDelay());
        }
        if (boxDTO.getLastHeartbeatTime() != null) {
            boxDO.setLastHeartbeatTime(boxDTO.getLastHeartbeatTime());
        }
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
     * 查询盒子列表
     *
     * @param queryBoxDTO 查询条件参数
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<List<BoxDTO>> listBoxes(QueryBoxDTO queryBoxDTO) {
        Specification<BoxDO> spec = (root, criteriaQuery, criteriaBuilder) -> {
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
            if (queryBoxDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryBoxDTO.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        try {
            List<BoxDO> boxes = boxRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
            List<BoxDTO> dtos = boxes.stream().map(this::convertToDto).toList();
            log.info("查询盒子列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询盒子列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    /**
     * 分页查询盒子列表
     *
     * @param queryBoxDTO 查询条件参数
     * @param pageQueryDTO 分页条件参数
     * @return RestResult 结果封装
     */
    @Override
    public RestResult<PageResult<BoxDTO>> listBoxesByPage(QueryBoxDTO queryBoxDTO, PageQueryDTO pageQueryDTO) {
        Specification<BoxDO> spec = (root, criteriaQuery, criteriaBuilder) -> {
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
            if (queryBoxDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryBoxDTO.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        try {
            // 构造排序对象
            Sort sort = Sort.by(Sort.Direction.fromString(pageQueryDTO.getSortOrder()), pageQueryDTO.getSortField());
            // 执行分页查询
            org.springframework.data.domain.Page<BoxDO> boxPage = boxRepository.findAll(spec, 
                org.springframework.data.domain.PageRequest.of(pageQueryDTO.getPage() - 1, pageQueryDTO.getSize(), sort));
            
            // 转换为DTO列表
            List<BoxDTO> dtos = boxPage.getContent().stream().map(this::convertToDto).toList();
            
            // 封装分页结果
            PageResult<BoxDTO> pageResult = new PageResult<>(
                pageQueryDTO.getPage(), 
                pageQueryDTO.getSize(), 
                boxPage.getTotalElements(), 
                dtos
            );
            
            log.info("分页查询盒子列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询盒子列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
    }
    }

    /**
     * 将DTO转换为DO
     *
     * @param dto DTO对象
     * @return DO对象
     */
    private BoxDO convertToEntity(BoxDTO dto) {
        BoxDO entity = new BoxDO();
        entity.setId(dto.getId());
        entity.setBoxCode(dto.getBoxCode());
        entity.setUserId(dto.getUserId());
        entity.setBoxName(dto.getBoxName());
        entity.setStatus(dto.getStatus());
        entity.setRssi(dto.getRssi());
        entity.setBattery(dto.getBattery());
        entity.setLastHeartbeatTime(dto.getLastHeartbeatTime());
        return entity;
    }

    /**
     * 将DO转换为DTO
     *
     * @param entity DO对象
     * @return DTO对象
     */
    private BoxDTO convertToDto(BoxDO entity) {
        BoxDTO dto = new BoxDTO();
        dto.setId(entity.getId());
        dto.setBoxCode(entity.getBoxCode());
        dto.setUserId(entity.getUserId());
        dto.setBoxName(entity.getBoxName());
        dto.setStatus(entity.getStatus());
        dto.setRssi(entity.getRssi());
        dto.setBattery(entity.getBattery());
        dto.setLastHeartbeatTime(entity.getLastHeartbeatTime());
        return dto;
    }
}