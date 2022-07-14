package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/14 10:53
 */
@Data
public abstract class BaseBody implements Serializable {

    private Integer tag = 1;

    public abstract Byte code();

}
