package org.hints.im.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.nutz.dao.entity.Record;
import org.nutz.lang.util.NutMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/5 12:36
 */
@RestController
public class LoginController {


    @PostMapping("/login")
    public Object loginCommon(@RequestParam("username") String username,@RequestParam("password") String password, HttpServletResponse response) throws Exception {

        NutMap result = doGet("http://localhost:6001/oauth/token?username=" + username + "&password=" + password + "&grant_type=password&scope=getuserinfo1&client_id=client_4&client_secret=123456");
        JSONObject jsonObject = (JSONObject)result.get("data");
        if (result.getInt("code") == HttpStatus.SC_OK) {
            javax.servlet.http.Cookie cookies1 = new javax.servlet.http.Cookie("access_token", jsonObject.getString("access_token"));
            cookies1.setPath("/");
            cookies1.setMaxAge(jsonObject.getIntValue("expires_in"));
            response.addCookie(cookies1);

            javax.servlet.http.Cookie cookies2 = new javax.servlet.http.Cookie("refresh_token", jsonObject.getString("refresh_token"));
            cookies2.setPath("/");
            response.addCookie(cookies2);
        } else {
        }
        return result;
    }

    public static NutMap doGet(String url) {
        NutMap result = NutMap.NEW();
        Cookie resCookie = null;
        BasicCookieStore store = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(store).build();
        CloseableHttpResponse response = null;
        String resultBody = "";
        int resultCode = 0;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            resultCode = response.getStatusLine().getStatusCode();
            resultBody = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result.setv("code", resultCode);
        result.setv("data", JSON.parseObject(resultBody));
        result.setv("msg", "");
        return result;
    }
}
