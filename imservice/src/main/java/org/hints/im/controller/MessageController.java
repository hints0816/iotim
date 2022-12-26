package org.hints.im.controller;

import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.entity.GroupHistoryDO;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/29 18:26
 */

@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private Dao dao;

    @GetMapping("/history")
    public ReturnVo history(String groupId,@RequestParam("page_num") Integer pageNum, @RequestParam("page_size") Integer pageSize) {
        Sql sql1 = Sqls.create("SELECT T1.*,T2.NICK_NAME,T2.AVATER FROM CHAT_GROUP_HISTORY T1, SYS_USER T2 WHERE " +
                "T1.FROM_ID = T2.USER_NAME " +
                "AND GROUP_ID = @GROUPID ORDER BY TIME DESC");

        sql1.setParam("GROUPID", groupId);
        sql1.setPager(new Pager(pageNum, pageSize));
        sql1.setCallback(Sqls.callback.entities());
        sql1.setEntity(dao.getEntity(Record.class));
        List<Record> history = dao.execute(sql1).getList(Record.class);

        Sql sql = Sqls.create("SELECT SU.USER_NAME, SU.NICK_NAME, SU.AVATER FROM SYS_USER SU, SYS_GROUP_MEMBER SGM " +
                "WHERE SU.USER_NAME = SGM.USER_ID AND SGM.GROUP_ID = @GROUPID");

        sql.setParam("GROUPID", groupId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Record.class));
        List<Record> members = dao.execute(sql).getList(Record.class);


        Sql sql2 = Sqls.create("SELECT T1.*,T2.NICK_NAME,T2.AVATER FROM SYS_GROUP T1, SYS_USER T2 WHERE T1.OWNER = T2.USER_NAME AND T1.GROUP_ID = @GROUPID");

        sql2.setParam("GROUPID", groupId);
        sql2.setCallback(Sqls.callback.entities());
        sql2.setEntity(dao.getEntity(Record.class));
        List<Record> group = dao.execute(sql2).getList(Record.class);

        NutMap addv = NutMap.NEW().addv("history", history).addv("members", members).addv("group", group);

        return ReturnVo.success(addv);
    }
}
