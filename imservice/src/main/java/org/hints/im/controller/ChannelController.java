package org.hints.im.controller;

import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.entity.GroupDTO;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.channels.Channel;
import java.time.LocalDateTime;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/9/9 16:14
 */

@RestController
@RequestMapping(value = "/channel")
public class ChannelController {

    @Autowired
    private Dao dao;

    @GetMapping("/info/{groupId}")
    public ReturnVo info(@PathVariable String groupId) {
        GroupDTO groupInfo = dao.fetch(GroupDTO.class, Cnd.where("group_id", "=", groupId));
        return ReturnVo.success(groupInfo);
    }

    @PostMapping("/updatename")
    public ReturnVo updateName(GroupDTO groupDTO) {
        int update = dao.update(GroupDTO.class, Chain.make("name", groupDTO.getName()),
                Cnd.where("group_id", "=", groupDTO.getGroupId()));
        return update>0?ReturnVo.success():ReturnVo.error();
    }

    @PostMapping("/invitefriend")
    public ReturnVo inviteFriend(String user_name, String group_id) {
        dao.insert("sys_group_member", Chain.make("group_id",group_id).add("user_id",user_name).add("adddate", LocalDateTime.now()));
        return ReturnVo.success();
    }

}
