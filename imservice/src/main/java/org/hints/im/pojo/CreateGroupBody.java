package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/23 17:05
 */
@Data
public class CreateGroupBody extends BaseBody implements Serializable {

    private List<String> userIdList;

    public CreateGroupBody() {

    }

    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_REQUEST;
    }
}
