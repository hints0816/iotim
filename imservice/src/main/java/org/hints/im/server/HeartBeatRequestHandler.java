package org.hints.im.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/27 16:46
 */
@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<Object> {

    public static HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

    private HeartBeatRequestHandler () {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object heartBeatRequestPacket) throws Exception {
    }


}