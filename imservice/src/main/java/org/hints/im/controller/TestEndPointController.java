package org.hints.im.controller;

import io.netty.channel.Channel;
import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.entity.GroupDTO;
import org.hints.im.pojo.entity.HistoryDO;
import org.hints.im.utils.MinIoUtil;
import org.hints.im.utils.SessionUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.dao.util.cri.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
public class TestEndPointController {

    Logger logger = LoggerFactory.getLogger(TestEndPointController.class);

    @Autowired
    private Dao dao;

    @GetMapping("/getInfo")
    public OAuth2Authentication getInfo(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {
        logger.info(oAuth2Authentication.getUserAuthentication().getAuthorities().toString());
        logger.info(oAuth2Authentication.toString());
        logger.info("principal.toString()"+principal.toString());
        logger.info("principal.getName()"+principal.getName());
        logger.info("authentication:"+authentication.getAuthorities().toString());
        return oAuth2Authentication;
    }

    @GetMapping("/userlist")
    public List<Record> userlist(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {
        List<Record> list = dao.query("sys_user", null);
        return list;
    }

    @GetMapping("/history")
    public ReturnVo history(Integer fromUsername, Principal principal) {
        String name = principal.getName();
        List<HistoryDO> query = dao.query(HistoryDO.class,
                Cnd.where(new Static("((from_id = "+ name +" and to_id = "+ fromUsername +") or (from_id = "+ fromUsername +" and to_id = "+ name +"))")).orderBy("time", "asc"));
        return ReturnVo.success(query);
    }

    @GetMapping("/creategroup")
    public ReturnVo creategroup(GroupDTO groupDTO, Principal principal) {
        String name = principal.getName();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        groupDTO.setGroupId(uuid);

        groupDTO.setOwner(name);
        groupDTO.setType(1);

        return ReturnVo.success();
    }

    @GetMapping("/upload")
    public ReturnVo upload(MultipartFile file, Principal principal) {
        String upload = MinIoUtil.upload("im", "gscm", file);
        return ReturnVo.error();
    }

}
