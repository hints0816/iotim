package org.hints.im.pojo.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Table;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 17:38
 */
@Data
@Table("CHAT_HISTORY")
public class HistoryDO {

    private String content;
    private Long from_id;
    private Long to_id;
    private Long type;
    private Long time;
    private String msg_id;
}
