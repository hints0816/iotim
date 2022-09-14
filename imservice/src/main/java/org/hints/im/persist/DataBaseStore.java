package org.hints.im.persist;

import lombok.extern.slf4j.Slf4j;
import org.hints.im.pojo.CreateGroupBody;
import org.hints.im.pojo.MsgBody;
import org.hints.im.pojo.entity.GroupDTO;
import org.hints.im.pojo.entity.GroupHistoryDO;
import org.hints.im.pojo.entity.HistoryDO;
import org.hints.im.server.ThreadPoolExecutorWrapper;
import org.nutz.dao.Chain;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/11 15:38
 */
@Slf4j
@Component
public class DataBaseStore {

    @Autowired
    private Dao dao;
    private ThreadPoolExecutorWrapper dbScheduler;

    public DataBaseStore(){
        log.info("init dbScheduler ... ");
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        dbScheduler = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "db");
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
        historyDO.setFile_type(msgBody.getFileType());

        dbScheduler.execute(()-> {
            dao.insert(historyDO);
        });
    }

    public void createGroup(CreateGroupBody createGroupBody) {
        String groupId = UUID.randomUUID().toString().replaceAll("-", "");

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupId(groupId);
        groupDTO.setOwner(createGroupBody.getOwner());
        groupDTO.setName(createGroupBody.getName());
        groupDTO.setType(2);
        groupDTO.setMembernum(20L);
        groupDTO.setUserlist(createGroupBody.getUserIdList());
        Date date = new Date();
        groupDTO.setCreatedate(date);

        dbScheduler.execute(()-> {
            List<Long> userIdList = createGroupBody.getUserIdList();
            dao.insert(groupDTO);
            for (Long aLong : userIdList) {
                dao.insert("sys_group_member", Chain.make("group_id",groupId).add("user_id",aLong).add("adddate",date));
            }
        });
    }

    public void persistGroup(GroupHistoryDO groupHistoryDO) {
        dbScheduler.execute(()-> {
            dao.insert(groupHistoryDO);
        });
    }

}
