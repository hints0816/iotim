package org.hints.im.controller;

import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.entity.GroupDTO;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author 180686
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
}
