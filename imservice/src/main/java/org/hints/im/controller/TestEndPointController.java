package org.hints.im.controller;

import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.User;
import org.hints.im.utils.FileContentTypeUtils;
import org.hints.im.utils.MinIoUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@RestController
public class TestEndPointController {

    Logger logger = LoggerFactory.getLogger(TestEndPointController.class);

    @Autowired
    private Dao dao;

    @GetMapping("/getInfo")
    public Object getInfo(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {
        logger.info(oAuth2Authentication.getUserAuthentication().getAuthorities().toString());
        logger.info(oAuth2Authentication.toString());

        User user_name = dao.fetch(User.class, Cnd.where("user_name", "=", principal.getName()));

        return user_name;
    }

    @GetMapping("/userlist")
    public ReturnVo userlist(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {

        Sql sql = Sqls.create("SELECT * FROM (\n" +
                "SELECT T1.*,T2.NAME,T2.IMG AS AVATER,'1' AS MSGTYPE, NICK_NAME AS FROM_NAME FROM (SELECT FROM_ID,TYPE,TIME,CONTENT,GROUP_ID AS TARGET,FILE_TYPE FROM (  \n" +
                "    SELECT ROW_NUMBER() OVER(PARTITION BY GROUP_ID ORDER BY TIME DESC) RN,         \n" +
                "           T.*         \n" +
                "      FROM (SELECT * FROM CHAT_GROUP_HISTORY WHERE GROUP_ID IN (SELECT GROUP_ID FROM SYS_GROUP_MEMBER WHERE USER_ID = @USER_ID)) T\n" +
                ") WHERE RN = 1) T1, SYS_GROUP T2, SYS_USER T3 WHERE T1.TARGET = T2.GROUP_ID AND T1.FROM_ID = T3.USER_NAME UNION ALL SELECT T1.*,T2.NICK_NAME AS NAME,T2.AVATER,'2' AS MSGTYPE, '' AS FROM_NAME FROM (SELECT FROM_ID,TYPE,TIME,CONTENT,TO_CHAR(DECODE(FROM_ID,@USER_ID,TO_ID,FROM_ID)) AS TARGET,FILE_TYPE FROM (  \n" +
                "    SELECT ROW_NUMBER() OVER(PARTITION BY FID ORDER BY TIME DESC) RN,         \n" +
                "           T.*         \n" +
                "      FROM (SELECT T1.*,T2.FID FROM CHAT_HISTORY T1, CHAT_FRIEND T2 WHERE ((T1.FROM_ID = T2.FROM_ID AND T1.TO_ID = T2.TO_ID)\n" +
                "       OR (T1.FROM_ID = T2.TO_ID AND T1.TO_ID = T2.FROM_ID)) AND (T2.FROM_ID = @USER_ID OR T2.TO_ID = @USER_ID)) T\n" +
                ") WHERE RN = 1) T1, SYS_USER T2 WHERE T1.TARGET = T2.USER_NAME ) ORDER BY TIME DESC");

        sql.setParam("USER_ID", principal.getName());

        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Record.class));
        List<Record> list = dao.execute(sql).getList(Record.class);
        for (Record record : list) {
            String content ="";
            if(record.getInt("MSGTYPE") == 1){
                content+= record.getString("FROM_NAME")+" : ";
            }
            if("1".equals(record.getString("file_type"))){
                record.set("content", content+"[ picture ]");
            }
        }
        return ReturnVo.success(NutMap.NEW().addv("users", list));
    }

    @GetMapping("/channellist")
    public ReturnVo channellist(OAuth2Authentication oAuth2Authentication, Principal principal, Authentication authentication) {

        Sql sql = Sqls.create("SELECT * FROM (\n" +
                "SELECT T1.*,T2.NAME,T2.IMG AS AVATER,'1' AS MSGTYPE FROM (SELECT FROM_ID,TYPE,TIME,CONTENT,GROUP_ID AS TARGET FROM (  \n" +
                "    SELECT ROW_NUMBER() OVER(PARTITION BY GROUP_ID ORDER BY TIME DESC) RN,         \n" +
                "           T.*         \n" +
                "      FROM (SELECT * FROM CHAT_GROUP_HISTORY WHERE GROUP_ID IN (SELECT GROUP_ID FROM SYS_GROUP_MEMBER WHERE USER_ID = @USER_ID)) T\n" +
                ") WHERE RN = 1) T1, SYS_GROUP T2 WHERE T1.TARGET = T2.GROUP_ID) ORDER BY TIME DESC");

        sql.setParam("USER_ID", principal.getName());

        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Record.class));
        List<Record> list = dao.execute(sql).getList(Record.class);

        return ReturnVo.success(NutMap.NEW().addv("users", list));
    }


    @GetMapping("/showfriends")
    public ReturnVo showfriends(Principal principal) {

        Sql sql = Sqls.create("SELECT T1.* FROM SYS_USER T1," +
                "(SELECT TO_CHAR(DECODE(FROM_ID,@USERNAME,TO_ID,FROM_ID)) AS TARGET " +
                "FROM CHAT_FRIEND WHERE (FROM_ID = @USERNAME OR TO_ID = @USERNAME)) T2 WHERE T1.USER_NAME = T2.TARGET");

        sql.setParam("USERNAME", principal.getName());

        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(User.class));
        List<User> list = dao.execute(sql).getList(User.class);

        return ReturnVo.success(NutMap.NEW().addv("users", list));
    }

    @GetMapping("/history")
    public ReturnVo history(Integer fromUsername, Principal principal) {
        String name = principal.getName();
        Sql sql = Sqls.create("SELECT T1.*,T2.NICK_NAME,T2.AVATER FROM CHAT_HISTORY T1, SYS_USER T2 WHERE " +
                "T1.FROM_ID = T2.USER_NAME " +
                "AND ((FROM_ID = @NAME AND TO_ID = @FROMUSERNAME) OR (FROM_ID = @FROMUSERNAME AND TO_ID = @NAME)) ORDER BY TIME ASC");

        sql.setParam("NAME", name);
        sql.setParam("FROMUSERNAME", fromUsername);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Record.class));
        List<Record> list = dao.execute(sql).getList(Record.class);

        return ReturnVo.success(list);
    }

    @PostMapping("/upload")
    public ReturnVo upload(MultipartFile file, Principal principal) {
        String upload = "";
        if(file.getContentType().startsWith("image")){
            upload = MinIoUtil.upload("img", "gscm", file);
        }else{
            upload = MinIoUtil.upload("file", "gscm", file);
        }
        return ReturnVo.success(upload);
    }

    //TODO 非图床
    @PostMapping("/download")
    public void download(String name, HttpServletResponse response) {
        MinIoUtil.download("gscm", "img/" + name, response);
    }


}
