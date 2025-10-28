package org.example.boxes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 盒子编码
     */
    @NotBlank(message = "盒子编码不能为空")
    @Size(max = 32, message = "盒子编码长度不能超过32个字符")
    private String boxCode;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 盒子名称
     */
    @NotBlank(message = "盒子名称不能为空")
    @Size(max = 50, message = "盒子名称长度不能超过50个字符")
    private String boxName;

    /**
     * 盒子状态（0-离线 1-在线）
     */
    @NotNull(message = "盒子状态不能为空")
    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status;

    /**
     * RSSI信号强度（-100到0）
     */
    @Min(value = -100, message = "RSSI值超出范围")
    @Max(value = 0, message = "RSSI值超出范围")
    private Integer rssi;

    /**
     * 电池电量（0-100）
     */
    @Min(value = 0, message = "电量值超出范围")
    @Max(value = 100, message = "电量值超出范围")
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
