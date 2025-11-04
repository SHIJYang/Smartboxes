package org.example.boxes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误日志数据库实体类
 *
 * @author 14577
 */
@Entity
@Table(name = "error_log")  // 注意：表名与其他表不一致，建议统一为 t_error_log
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDO {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 错误编码
     */
    @Column(name = "error_code", nullable = false)
    private String errorCode;

    /**
     * 错误信息
     */
    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    /**
     * 错误时间
     */
    @Column(name = "error_time", nullable = false)
    private LocalDateTime errorTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}