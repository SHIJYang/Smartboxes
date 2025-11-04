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
import org.example.boxes.repository.BoxRepository;
import org.example.boxes.result.PageResult;
import org.example.boxes.result.RestResult;
import org.example.boxes.service.BoxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class BoxServiceImpl implements BoxService {

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

        // 确保 ID 为 null，表示新增操作
        boxDO.setId(null);

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
        Specification<BoxDO> spec = buildSpecification(queryBoxDTO);

        try {
            List<BoxDO> boxes = boxRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
            List<BoxDTO> dtos = boxes.stream().map(this::convertToDto).toList();
            log.info("查询盒子列表成功，共 {} 条记录", dtos.size());
            return RestResult.success(dtos);
        } catch (Exception e) {
            log.error("查询盒子列表异常：{}", e.getMessage(), e);
            return RestResult.error("系统错误");
        }
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
        Specification<BoxDO> spec = buildSpecification(queryBoxDTO);

        try {
            // 设置默认值
            int page = pageQueryDTO.getPage() != null ? pageQueryDTO.getPage() - 1 : 0;
            int size = pageQueryDTO.getSize() != null ? pageQueryDTO.getSize() : 10;
            String sortOrder = pageQueryDTO.getSortOrder() != null ? pageQueryDTO.getSortOrder() : "DESC";
            String sortField = pageQueryDTO.getSortField() != null ? pageQueryDTO.getSortField() : "createTime";

            // 构造排序对象
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);

            // 执行分页查询
            Page<BoxDO> boxPage = boxRepository.findAll(spec, PageRequest.of(page, size, sort));

            // 转换为DTO列表
            List<BoxDTO> dtos = boxPage.getContent().stream().map(this::convertToDto).toList();

            // 封装分页结果
            PageResult<BoxDTO> pageResult = new PageResult<>(
                    page + 1,
                    size,
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

    /**
     * 构建查询条件
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
            // 修改这里：对于boxType，使用等值查询
            if (queryBoxDTO.getBoxType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("boxType"), queryBoxDTO.getBoxType()));
            }
            if (queryBoxDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryBoxDTO.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 将实体转换为DTO
     */
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
        boxDTO.setLastHeartbeatTime(boxDO.getLastHeartbeatTime());
        boxDTO.setCreateTime(boxDO.getCreateTime());
        boxDTO.setUpdateTime(boxDO.getUpdateTime());
        return boxDTO;
    }

    /**
     * 将DTO转换为实体
     */
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
        boxDO.setLastHeartbeatTime(boxDTO.getLastHeartbeatTime());
        boxDO.setCreateTime(boxDTO.getCreateTime());
        boxDO.setUpdateTime(boxDTO.getUpdateTime());
        return boxDO;
    }
}