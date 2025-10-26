package org.example.boxes.repository;

import java.util.Optional;
import org.example.boxes.entity.ItemDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 商品仓库接口，继承JpaRepository和JpaSpecificationExecutor以获得基础查询能力及复杂查询支持
 *
 * @author 14577
 */
@Repository
public interface ItemRepository extends JpaRepository<ItemDO, Long>, JpaSpecificationExecutor<ItemDO> {

    /**
     * 根据商品编码查找是否存在对应的商品记录
     *
     * @param itemCode 商品编码
     * @return Optional<ItemDO>
     */
    Optional<ItemDO> findByItemCode(String itemCode);
}