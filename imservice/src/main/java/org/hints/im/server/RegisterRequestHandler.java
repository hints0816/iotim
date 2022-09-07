package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.manager.util.SessionUtils;
import org.hints.im.pojo.BaseBody;
import org.hints.im.pojo.LoginBody;
import org.hints.im.utils.SessionUtil;

import java.nio.charset.Charset;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/25 9:58
 */
@ChannelHandler.Sharable
@Slf4j
public class RegisterRequestHandler extends SimpleChannelInboundHandler<LoginBody> {

    public static RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    private RegisterRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginBody loginBody) throws Exception {

        ByteBuf byteBuf = ctx.alloc().buffer();
        String fromUser = SessionUtil.getUser(ctx.channel()).getUserName();
        JSONObject data = new JSONObject();
        data.put("type", 1);
        data.put("status", 200);
        JSONObject params = new JSONObject();
        params.put("message", fromUser + ":login success");
        params.put("fromUser", fromUser);
        data.put("params", params);
        byte[] bytes = data.toJSONString().getBytes(Charset.forName("utf-8"));
        byteBuf.writeBytes(bytes);


        ctx.channel().writeAndFlush(new TextWebSocketFrame(byteBuf));
    }

}
