package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

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
    protected void channelRead0(ChannelHandlerContext ctx, Object heartBeatRequestPacket) throws Exception {// TODO Auto-generated method stub
        ByteBuf byteBuf = getBuf(ctx);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(byteBuf));
    }

    public ByteBuf getBuf(ChannelHandlerContext ctx) {
        ByteBuf byteBuf = ctx.alloc().buffer();
        JSONObject data = new JSONObject();
        data.put("type", 12);
        data.put("status", 200);
        byte bytes[] = data.toJSONString().getBytes();
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }


}