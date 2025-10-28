package org.example.boxes.repository;

import org.example.boxes.entity.ErrorLogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 错误日志数据访问接口
 *
 * @author 14577
 */
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLogDO, Long> {
}