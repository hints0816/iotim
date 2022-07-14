package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import com.gree.chat.pojo.MsgBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 180686 on 2022/5/9 17:28
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // 保存id和容器的关系
    private static Map<String, ChannelHandlerContext> map = new HashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("ServerHandler channelActive()" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("ServerHandler channelInactive()");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        Channel channel = channelHandlerContext.channel();
        ChannelId channelId = channel.id();
        map.put(channelId.toString(), channelHandlerContext);
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) o;
        String msg = textWebSocketFrame.text();
        System.out.println("msg:" + msg);
        ByteBuf byteBuf = (ByteBuf) o;
        String rev = getMessage(byteBuf);
        MsgBody msgBody = JSONObject.parseObject(rev, MsgBody.class);
        String format = String.format("服务器接收到客户端消息，发送人：%s, 发送消息：%s .", msgBody.getSendUserName(), msgBody.getMsg());
        System.out.println(format);

        map.forEach((k, v) -> {
            try {
                // 出现问题1的原因：遇到不是自身的客户端过滤掉了，但实际上返回消息是需要自身的
                if (channelId.toString().equals(k)) {
                    return;
                }

                MsgBody sendMsgBody = new MsgBody();
                sendMsgBody.setSendUserName(msgBody.getSendUserName());
                sendMsgBody.setMsg(msgBody.getMsg());
                v.writeAndFlush(getSendByteBuf(JSONObject.toJSONString(sendMsgBody)));
                System.out.println("服务端回复消息： " + JSONObject.toJSONString(sendMsgBody));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 从ByteBuf 中获取信息，使用UTF-8编码返回
     *
     * @param buf
     * @return
     */
    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);

        try {
            return new String(con, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ByteBuf getSendByteBuf(String message) {
        byte[] req = message.getBytes();
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }
}