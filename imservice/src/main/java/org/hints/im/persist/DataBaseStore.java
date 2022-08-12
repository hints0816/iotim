package org.hints.im.persist;

import lombok.extern.slf4j.Slf4j;
import org.hints.im.pojo.MsgBody;
import org.hints.im.pojo.entity.HistoryDO;
import org.hints.im.server.NettyServer;
import org.hints.im.server.ThreadPoolExecutorWrapper;
import org.hints.im.utils.SpringUtils;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 15:38
 */
@Slf4j
public class DataBaseStore {

    private Dao dao;
    private ThreadPoolExecutorWrapper mScheduler;

    public DataBaseStore(ThreadPoolExecutorWrapper mScheduler){
        this.dao = SpringUtils.getBean(Dao.class);
        this.mScheduler = mScheduler;
    }

    public void persistMessage(MsgBody msgBody) {
        HistoryDO historyDO = new HistoryDO();
        historyDO.setFrom_id(Long.valueOf(msgBody.getFromUserId()));
        historyDO.setMsg_id(msgBody.getMsgId());
        Date date = new Date();
        historyDO.setTime(date.getTime());
        historyDO.setContent(msgBody.getMessage());
        historyDO.setTo_id(Long.valueOf(msgBody.getToUserId()));
        historyDO.setType(1L);

        mScheduler.execute(()-> {
            HistoryDO insert = dao.insert(historyDO);
        });
    }

}
