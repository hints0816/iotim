package org.hints.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ResourceLeakDetector;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by hints on 2022/5/9 17:28
 */
@Component
public class NettyServer{

    private final static String BANNER =
            "welcome!hongkongdoll!";
    private static NettyServer instance;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ThreadPoolExecutorWrapper dbScheduler;

    /*** 资源关闭--在容器销毁是关闭 */
    @PreDestroy
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static {
        System.out.println(BANNER);
    }

    public void run() {
        // config
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        this.startServer();
    }

//    private DataBaseStore initStore(NettyServer nettyServer) {
//        return initDataBaseStore(nettyServer.dbScheduler);
//    }

    public void startServer() {
        //1. 初始化数据库-oracle

        //2. 初始化AES对称加密密钥

        //3. 初始化线程池

        //(1).数据库
        //(2).业务
        //(3).回调

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        try {
            // 创建启动类
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            NettyServerHandler serverHandler = new NettyServerHandler(null);
                            MoquetteIdleTimeoutHandler timeoutHandler = new MoquetteIdleTimeoutHandler();

                            // 添加NettyServerHandler，用来处理
                            channelPipeline
                                    .addLast(new MoquetteIdleTimeoutHandler())
                                    .addLast("http-codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(65536))
                                    .addLast("compressor", new HttpContentCompressor())
                                    .addLast("http-chunked", new ChunkedWriteHandler())

                                    .addLast(HttpRequestHandler.INSTANCE)
                                    .addLast(RegisterRequestHandler.INSTANCE)
                                    .addLast(MessageRequestHandler.INSTANCE)
                                    .addLast(CreateGroupRequestHandler.INSTANCE)
                                    .addLast(GroupMessageRequestHandler.INSTANCE)
                                    .addLast(HeartBeatRequestHandler.INSTANCE)
                                    .addLast(ExceptionHandler.INSTANCE);
                        }
                    });


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

}