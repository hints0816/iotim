package org.hints.im.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/25 9:58
 */
@ChannelHandler.Sharable
@Slf4j
public class RegisterRequestHandler extends SimpleChannelInboundHandler<Object> {

    public static RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    private RegisterRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("当前登录人与channel绑定关系");
    }
}
