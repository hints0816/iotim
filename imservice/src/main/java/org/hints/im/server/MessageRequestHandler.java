package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.hints.im.persist.DataBaseStore;
import org.hints.im.pojo.MsgBody;
import org.hints.im.utils.SessionUtil;
import org.hints.im.utils.SpringUtils;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/26 12:45
 */
@ChannelHandler.Sharable
public class MessageRequestHandler extends SimpleChannelInboundHandler<MsgBody> {

    private KafkaTemplate kafkaTemplate;

    public static MessageRequestHandler INSTANCE = new MessageRequestHandler();

    private MessageRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgBody msgBody) throws Exception {
        // TODO Auto-generated method stub
        String message = "";
        Channel toUserChannel = SessionUtil.getChannel(msgBody.getToUserId());
        // 1.直接先落到oracle，生产环境处理高并发使用 kafka->mongodb
        String fromUser = SessionUtil.getUser(ctx.channel());
        msgBody.setFromUserId(fromUser);
        this.kafkaTemplate = SpringUtils.getBean(KafkaTemplate.class);
        kafkaTemplate.send("test",  JSONObject.toJSONString(msgBody));

        if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
            // 用户在线
            message = msgBody.getMessage();
            String toUser = SessionUtil.getUser(toUserChannel);
            String fileType = msgBody.getFileType();
            ByteBuf buf = getByteBuf(ctx, message, toUser, fileType);
            toUserChannel.writeAndFlush(new TextWebSocketFrame(buf));
        } else {
            message = "当前用户："+msgBody.getToUserId()+"不在线！";
            System.err.println(message);
        }
    }

    public ByteBuf getByteBuf(ChannelHandlerContext ctx, String message, String toUser, String fileType) {
        ByteBuf byteBuf = ctx.alloc().buffer();
        String fromUser = SessionUtil.getUser(ctx.channel());
        JSONObject data = new JSONObject();
        data.put("type", 2);
        data.put("status", 200);
        JSONObject params = new JSONObject();
        params.put("message", message);
        params.put("fileType", fileType);
        params.put("fromUser", fromUser);
        params.put("toUser", toUser);
        data.put("params", params);
        byte []bytes = data.toJSONString().getBytes(Charset.forName("utf-8"));
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

}