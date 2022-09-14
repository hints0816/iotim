package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/27 16:45
 */
@ChannelHandler.Sharable
@Slf4j
public class ExceptionHandler extends ChannelDuplexHandler {

    public static ExceptionHandler INSTANCE = new ExceptionHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        if (cause instanceof RuntimeException) {
            log.info("pipeline全局异常处理 Handle Business Exception Success.");
            ByteBuf byteBuf = ctx.alloc().buffer();
            JSONObject data = new JSONObject();
            data.put("type", 500);
            data.put("status", 500);
            byteBuf.writeBytes(data.toJSONString().getBytes());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(byteBuf));
        }
    }
}
