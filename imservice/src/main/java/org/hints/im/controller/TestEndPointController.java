package org.hints.im.controller;

import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.entity.HistoryDO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

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



}
