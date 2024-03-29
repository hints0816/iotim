package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hints on 2022/5/9 17:27
 */

@Data
public class MsgBody extends BaseBody implements Serializable {
    private String msgId;

    private String fromUserId;

    private String toUserId;

    private String message;

    private String fileType;

    public MsgBody() {
        super();
    }

    public MsgBody(String toUserId, String message) {
        super();
        this.toUserId = toUserId;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        // TODO Auto-generated method stub
        return Command.MESSAGE_REQUEST;
    }
}
