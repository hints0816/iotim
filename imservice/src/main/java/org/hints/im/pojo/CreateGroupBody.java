package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/23 17:05
 */
@Data
public class CreateGroupBody extends BaseBody implements Serializable {

    private List<Long> userIdList;

    private String name;

    private String avater;

    private Long owner;

    public CreateGroupBody() {

    }

    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_REQUEST;
    }
}
