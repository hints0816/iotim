package org.hints.im.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hints.im.pojo.MsgBody;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/26 12:45
 */
@ChannelHandler.Sharable
public class MessageRequestHandler extends SimpleChannelInboundHandler<MsgBody> {

    public static MessageRequestHandler INSTANCE = new MessageRequestHandler();

    private MessageRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgBody MsgBody) throws Exception {
        // TODO Auto-generated method stub
        String message = "";
        // 根据收件人ID来获取收件人的channel

        // 如果收件人的channel不在，则不在线
        // 如果收件人的channel在，则获取收件人的信息

    }

}