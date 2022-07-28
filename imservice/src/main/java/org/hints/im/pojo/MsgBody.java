package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by 180686 on 2022/5/9 17:27
 */

@Data
public class MsgBody extends BaseBody implements Serializable {
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

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public Byte getCommand() {
        // TODO Auto-generated method stub
        return Command.MESSAGE_REQUEST;
    }
}
