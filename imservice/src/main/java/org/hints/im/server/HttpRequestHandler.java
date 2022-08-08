package org.hints.im.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hints.im.utils.SessionUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/25 9:57
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    public static HttpRequestHandler INSTANCE = new HttpRequestHandler();

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest)msg);
            log.info("http 握手成功");
            ctx.channel().writeAndFlush(new TextWebSocketFrame("123213"));
        } else if (msg instanceof WebSocketFrame) {
            try  {
                handWebsocketFrame(ctx, (WebSocketFrame)msg);
            }catch (Exception ex) {
                throw new Exception();
            }
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws:/" + ctx.channel() + "/websocket", null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        this.handshaker = handshaker;
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {

//            HttpHeaders headers = req.headers();
//            String token = headers.get("Sec-WebSocket-Protocol");
//            Claims claims = null;
//            try {
//                claims = Jwts.parser()
//                        .setSigningKey("internet_plus".getBytes("utf-8"))
//                        .parseClaimsJws(token).getBody();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            String user_name = claims.get("user_name").toString();
//
//            SessionUtil.bindChannel(user_name, ctx.channel());
//            if (SessionUtil.hasLogin(ctx.channel())) {
//                System.out.println("该用户已登录");
//            }

            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭websocket的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
            return;
        }
        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;
        ByteBuf bytebuf = textWebSocketFrame.content();
        String content = bytebuf.toString(Charset.forName("utf-8"));
        JSONObject jsonObject = JSONObject.parseObject(content);
        System.err.println("请求参数："+jsonObject);
        Byte type = jsonObject.getByte("type");

        ctx.fireChannelRead("123");
    }
}

