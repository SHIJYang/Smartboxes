package org.example.boxes.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 统一返回结果封装类
 *
 * @author 14577
 * @param <T> 泛型参数表示返回的数据类型
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestResult<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String msg;

    private static final long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param data 返回的数据
     * @param <T>  泛型类型
     * @return RestResult 对象
     */
    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(200, "调用成功", data);
    }

    /**
     * 失败响应构建方法
     *
     * @param message 错误消息
     * @param <T>     泛型参数
     * @return 构建好的RestResult实例
     */
    public static <T> RestResult<T> error(String message) {
        return new RestResult<>(500, message, null);
    }

    /**
     * 成功响应，无返回数据
     *
     * @param <T> 泛型类型
     * @return RestResult 对象
     */
    public static <T> RestResult<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     *
     * @param msg 错误信息
     * @param <T> 泛型类型
     * @return RestResult 对象
     */
    public static <T> RestResult<T> fail(String msg) {
        return new RestResult<>(500, msg, null);
    }

    /**
     * 系统错误响应
     *
     * @param <T> 泛型类型
     * @return RestResult 对象
     */
    public static <T> RestResult<T> systemError() {
        return new RestResult<>(500, "系统异常", null);
    }
}
