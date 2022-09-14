package org.hints.im.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/11 16:47
 */
@Component
public class StartCommandLineRunner implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(String... args) {
        nettyServer.run();
    }
}
