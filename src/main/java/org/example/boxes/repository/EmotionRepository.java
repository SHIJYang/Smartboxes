
package org.example.boxes.repository;

import org.example.boxes.entity.EmotionDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 情感标签仓库接口，继承JpaRepository和JpaSpecificationExecutor以获得基础查询能力及复杂查询支持
 *
 * @author 14577
 */
@Repository
public interface EmotionRepository extends JpaRepository<EmotionDO, Long>, JpaSpecificationExecutor<EmotionDO> {
    
    /**
     * 根据物品ID查询所有情感标签
     *
     * @param itemId 物品ID
     * @return 情感标签列表
     */
    java.util.List<EmotionDO> findAllByItemId(Long itemId);

}
