package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/14 10:56
 */
@Data
public class LoginBody extends BaseBody implements Serializable {

    /**
     *
     */
    private String user;

    private String fromModule;


    @Override
    public Byte getCommand() {
        return null;
    }
}