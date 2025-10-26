package org.example.boxes.repository;

import java.util.Optional;
import org.example.boxes.entity.BoxDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 盒子仓库接口，继承JpaRepository和JpaSpecificationExecutor以获得基础查询能力及复杂查询支持
 *
 * @author 14577
 */
@Repository
public interface BoxRepository extends JpaRepository<BoxDO, Long>, JpaSpecificationExecutor<BoxDO> {

    /**
     * 根据盒子编码查找是否存在对应的盒子记录
     *
     * @param boxCode 盒子编码
     * @return Optional<BoxDO>
     */
    Optional<BoxDO> findByBoxCode(String boxCode);
}