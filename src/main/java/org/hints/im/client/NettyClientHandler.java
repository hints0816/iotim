package org.hints.im.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hints.im.pojo.MsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by 180686 on 2022/5/9 17:29
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private ByteBuf firstMessage;
    private ChannelHandlerContext ctx;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void sendMsg(String str) {
        byte[] data = str.getBytes();
        firstMessage = Unpooled.buffer();
        firstMessage.writeBytes(data);
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        LOGGER.info("userEventTriggered");

        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        MsgBody msgBody = new MsgBody();
        msgBody.setToUserId(userName);
        msgBody.setMessage("进入聊天室");
        byte[] data = JSONObject.toJSONString(msgBody).getBytes();
        firstMessage = Unpooled.buffer();
        firstMessage.writeBytes(data);
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端断开了，重新连接！");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Channel channel = ctx.channel();
        ByteBuf buf = (ByteBuf) o;
        String rev = getMessage(buf);
        MsgBody msgBody = JSONObject.parseObject(rev, MsgBody.class);
        String format = String.format("客户端收到服务端的消息，发送人:%s , 发送消息:%s .", msgBody.getToUserId(), msgBody.getMessage());
        System.out.println(format);
    }

    private String getMessage(ByteBuf buf) {
        byte[] conn = new byte[buf.readableBytes()];
        buf.readBytes(conn);
        try {
            return new String(conn, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}