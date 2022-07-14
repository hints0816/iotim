package org.hints.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import javax.annotation.PreDestroy;

/**
 * Created by 180686 on 2022/5/9 17:28
 */

public class NettyServer{
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();


    /*** 资源关闭--在容器销毁是关闭 */
    @PreDestroy
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void run() {
        try {
            // 创建启动类
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            NettyServerHandler serverHandler = new NettyServerHandler();
                            // 添加NettyServerHandler，用来处理
                            channelPipeline
                                    .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(8000))
                                    .addLast(new WebSocketServerProtocolHandler("/ws"))
                                    .addLast(serverHandler);
                        }
                    }) ;
            // 启动
            ChannelFuture future = boot.bind(9091).sync();
            System.out.println("--Netty服务端启动成功---");
            future.channel().closeFuture().sync() ;
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        final int port = 9091;
        run();
    }
}