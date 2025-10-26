package org.example.boxes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 盒子数据传输对象
 *
 * @author 14577
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxDTO {
    private Long id;
    private String boxCode;
    private Long userId;
    private String boxName;
    private Integer status;
    private Integer rssi;
    private Integer battery;
    private java.time.LocalDateTime lastHeartbeatTime;
    
    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;


}
