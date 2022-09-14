package org.hints.im.pojo.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 17:38
 */
@Data
@Table("CHAT_GROUP_HISTORY")
public class GroupHistoryDO {

    @Column(hump = true)
    private String content;

    @Column(hump = true)
    private Long fromId;

    @Column(hump = true)
    private String groupId;

    @Column(hump = true)
    private Long type;

    @Column(hump = true)
    private Long time;

    @Column(hump = true)
    private String msgId;

    @Column(hump = true)
    private String fileType;
}
