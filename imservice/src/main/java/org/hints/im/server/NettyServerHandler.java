package org.hints.im.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hints on 2022/5/9 17:28
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final ProtocolProcessor m_processor;

    public NettyServerHandler(ProtocolProcessor processor) {
        m_processor = processor;
    }

    // 保存id和容器的关系
    private static Map<String, ChannelHandlerContext> map = new HashMap<>();

    // 保存id和容器的关系
    private static ArrayList<Channel> list = new ArrayList<Channel>();

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        Channel channel = context.channel();
        map.put(channel.id().toString(), context);
        log.info("ServerHandler channelActive(){}", channel.remoteAddress());
        channel.writeAndFlush(new TextWebSocketFrame("login success"));
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
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {

        if (!(message instanceof MqttMessage)) {
            log.error("Unknown mqtt message type {}, {}", message.getClass().getName(), message);
            return;
        }

        Channel channel = channelHandlerContext.channel();
        ChannelId channelId = channel.id();
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)message;
        String msg = textWebSocketFrame.text();


        if (msg.startsWith("@")) {
            int i = msg.indexOf(" ");
            String id = msg.substring(1, i);
            String tomsg = msg.substring(i);
            System.out.println(tomsg);
            ChannelHandlerContext channelHandlerContext1 = map.get(id);
            channelHandlerContext1.channel().writeAndFlush(new TextWebSocketFrame(tomsg));
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(channelId).append(":").append(msg);
            channel.writeAndFlush(new TextWebSocketFrame(stringBuffer.toString()));
        }
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