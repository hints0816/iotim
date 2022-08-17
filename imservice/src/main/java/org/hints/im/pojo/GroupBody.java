package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/17 10:16
 */
@Data
public class GroupBody extends BaseBody implements Serializable {

    private String toGroupId;

    private String message;

    private String fileType;

    public GroupBody() {

    }

    @Override
    public Byte getCommand() {
        return Command.GROUP_MESSAGE_REQUEST;
    }
}
