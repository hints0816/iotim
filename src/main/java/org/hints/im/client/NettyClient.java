package org.hints.im.client;

import com.alibaba.fastjson.JSONObject;
import com.gree.chat.pojo.MsgBody;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Created by 180686 on 2022/5/9 17:28
 */

public class NettyClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private NettyClientHandler nettyClientHandler;

    private int port;
    private String host;
    private String sendUserName;
    private int errorCount;

    public NettyClient(int port, String host, String sendUserName) throws InterruptedException {
        this.port = port;
        this.host = host;
        this.sendUserName = sendUserName;
        start();
    }

    private void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .group(eventLoopGroup)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            nettyClientHandler = new NettyClientHandler();
                            nettyClientHandler.setUserName(sendUserName);
                            socketChannel.pipeline().addLast(nettyClientHandler);
                        }
                    });
            ChannelFuture channelFuture = null;

            try {
            channelFuture = bootstrap.connect(host, port).sync();
            } catch (Exception e) {
                errorCount++;
                if (errorCount >= 5) {
                    LOGGER.error("??????????????????????????????[{}]???", errorCount);
                }
                System.err.println(sendUserName + "?????????????????????");
            }
            if (channelFuture.isSuccess()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Scanner sc = new Scanner(System.in);
                        while (sc.hasNext()) {

                            MsgBody msgBody = new MsgBody();
                            msgBody.setSendUserName(sendUserName);
                            msgBody.setMsg(sc.next());
                            nettyClientHandler.sendMsg(JSONObject.toJSONString(msgBody));
                        }
                    }
                }).start();
                System.err.println(sendUserName + "?????????????????????");
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}