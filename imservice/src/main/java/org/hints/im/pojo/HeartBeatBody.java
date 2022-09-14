package org.hints.im.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/9/9 14:26
 */
@Data
public class HeartBeatBody  extends BaseBody implements Serializable {

    @Override
    public Byte getCommand() {
        // TODO Auto-generated method stub
        return Command.HEARTBEAT_REQUEST;
    }

}
