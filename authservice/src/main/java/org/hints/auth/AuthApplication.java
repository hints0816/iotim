package org.hints.auth;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/28 18:36
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAuthorizationServer
@MapperScan("org.hints.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}