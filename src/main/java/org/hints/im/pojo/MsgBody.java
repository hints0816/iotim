package org.hints.im.pojo;

import lombok.Data;

/**
 * Created by 180686 on 2022/5/9 17:27
 */

@Data
public class MsgBody {
    //发送人名称
    private String sendUserName;
    private String msg;
}
