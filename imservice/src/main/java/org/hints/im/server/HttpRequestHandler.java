package org.hints.im.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.hints.im.pojo.*;
import org.hints.im.utils.SessionUtil;
import org.hints.im.utils.SpringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/25 9:57
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    public static HttpRequestHandler INSTANCE = new HttpRequestHandler();

    private WebSocketServerHandshaker handshaker;

    private Dao dao;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest)msg);
            log.info("http 握手成功");
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

            String token = req.uri().substring(1);
            Claims claims = null;
            try {
                claims = Jwts.parser()
                        .setSigningKey("internet_plus".getBytes("utf-8"))
                        .parseClaimsJws(token).getBody();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String user_name = claims.get("user_name").toString();

            this.dao = SpringUtils.getBean(Dao.class);
            User user = this.dao.fetch(User.class, Cnd.where("user_name", "=", user_name));

            SessionUtil.bindChannel(user, ctx.channel());
            if (SessionUtil.hasLogin(ctx.channel())) {
                System.out.println("该用户已登录");
            }


            List<Record> groupDto = this.dao.query("sys_group_member", Cnd.where("user_id", "=", user_name));
            for (Record record : groupDto) {
                String group_id = record.getString("group_id");
                ChannelGroup channelGroup = SessionUtil.getChannelGroup(group_id);
                if(channelGroup == null) {
                    channelGroup = new DefaultChannelGroup(ctx.executor());
                    channelGroup.add(ctx.channel());
                    SessionUtil.bindChannelGroup(group_id, channelGroup);
                }else{
                    channelGroup.add(ctx.channel());
                }
            }

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
        Byte type = jsonObject.getByte("type");
        BaseBody baseBody = null;
        switch (type) {
            /*login initing*/
            case 1:
                LoginBody loginBody = new LoginBody();
                loginBody.setUser(SessionUtil.getUser(ctx.channel()).getUserName());
                baseBody = loginBody;
                break;
            case 2:
                MsgBody msgBody = new MsgBody();
                String toUserId = jsonObject.getString("toUserId");
                msgBody.setToUserId(toUserId);

                msgBody.setMsgId(jsonObject.getString("msgId"));
                String msg = jsonObject.getString("msg");
                msgBody.setMessage(msg);

                msgBody.setFileType(jsonObject.getString("fileType"));
                baseBody = msgBody;
                break;
            case 3:
                GroupBody groupBody = new GroupBody();
                groupBody.setMessage(jsonObject.getString("msg"));
                groupBody.setMsgId(jsonObject.getString("msgId"));
                groupBody.setToGroupId(jsonObject.getString("groupId"));
                groupBody.setFileType(jsonObject.getString("fileType"));
                baseBody = groupBody;
                break;
            case 4:
                CreateGroupBody createGroupBody = new CreateGroupBody();
                createGroupBody.setName(jsonObject.getString("name"));
                createGroupBody.setOwner(Long.valueOf(SessionUtil.getUser(ctx.channel()).getUserName()));
                createGroupBody.setAvater(jsonObject.getString("avater"));
                JSONArray userlist = jsonObject.getJSONArray("userlist");
                ArrayList<Long> longs = new ArrayList<>();
                for (Object o : userlist) {
                    Long o1 = Long.valueOf(o.toString());
                    longs.add(o1);
                }
                createGroupBody.setUserIdList(longs);
                baseBody = createGroupBody;
                break;
            case 11:
                HeartBeatBody heartBeatBody = new HeartBeatBody();
                baseBody = heartBeatBody;
                break;
            default:
                break;
        }

        ctx.fireChannelRead(baseBody);
    }
}

