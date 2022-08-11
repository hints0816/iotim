package org.hints.im.server;

import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 16:47
 */
@Component
public class StartImmediateExecutionCommandLineRunner  implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        nettyServer.run();

        System.out.println("第三种也是监听接口方式，启动服务...");
    }
}
