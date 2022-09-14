package org.hints.im;

import org.hints.im.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/7 9:04
 */
@EnableResourceServer
@SpringBootApplication
public class ImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImServiceApplication.class, args);
    }


}
