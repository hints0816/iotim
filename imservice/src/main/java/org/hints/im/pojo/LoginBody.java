package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/14 10:56
 */
@Data
public class LoginBody extends BaseBody implements Serializable {

    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public Byte getCommand() {
        return null;
    }
}