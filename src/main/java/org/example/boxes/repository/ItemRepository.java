package org.example.boxes.repository;

import java.util.List;
import java.util.Optional;
import org.example.boxes.entity.ItemDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 物品仓库接口
 *
 * @author 14577
 */
@Repository
public interface ItemRepository extends JpaRepository<ItemDO, Long>, JpaSpecificationExecutor<ItemDO> {

    /**
     * 根据物品编码查找
     */
    Optional<ItemDO> findByItemCode(String itemCode);

    /**
     * 1. 根据盒子ID查询物品 (对应：根据盒子找物品)
     */
    List<ItemDO> findByBoxId(Long boxId);

    /**
     * 2. 根据用户ID查询物品 (对应：根据用户找物品)
     * 利用 Box 关联关系：Item -> Box -> User
     */
    List<ItemDO> findByBox_UserId(Long userId);

    /**
     * 3. 根据用户ID和有效状态查询 (例如：查询某用户所有“在盒内”的物品)
     */
    List<ItemDO> findByBox_UserIdAndIsValid(Long userId, Integer isValid);
}