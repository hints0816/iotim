package org.hints.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/10 17:24
 */

@Data
@AllArgsConstructor
public class ReturnVo {

    private int code;

    private String msg;

    private Object data;

    public static ReturnVo success() {
        return ReturnVo.success("操作成功");
    }

    public static ReturnVo success(Object data) {
        return ReturnVo.success("操作成功", data);
    }

    public static ReturnVo success(String msg) {
        return ReturnVo.success(msg, null);
    }

    public static ReturnVo success(String msg, Object data) {
        return new ReturnVo(200, msg, data);
    }

    public static ReturnVo error() {
        return ReturnVo.error("操作失败");
    }

    public static ReturnVo error(String msg) {
        return ReturnVo.error(msg, null);
    }

    public static ReturnVo error(String msg, Object data) {
        return new ReturnVo(500, msg, data);
    }

    public static ReturnVo error(Integer code, String msg) {
        return new ReturnVo(code, msg, null);
    }

}
