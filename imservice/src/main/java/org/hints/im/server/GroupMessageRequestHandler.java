package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hints.game.Card;
import org.hints.game.Lobby;
import org.hints.game.Operate;
import org.hints.game.Player;
import org.hints.im.pojo.GroupBody;
import org.hints.im.pojo.User;
import org.hints.im.pojo.entity.GroupHistoryDO;
import org.hints.im.utils.SessionUtil;
import org.nutz.dao.Dao;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import org.springframework.kafka.core.KafkaTemplate;

/**
 * 发送群消息handler组件
 *
 * @author holiday
 * 2020-11-16
 */
@Sharable
@Slf4j
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupBody> {

    //	private KafkaTemplate kafkaTemplate;
    private Dao dao;


    public static GroupMessageRequestHandler INSTANCE = new GroupMessageRequestHandler();

    private GroupMessageRequestHandler() {

    }


    class RemindTask implements Runnable {
        private String groupId;

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        @Override
        public void run() {
            Lobby lobby = SessionUtil.getLobby(groupId);

            lobby.overTimePick();
            log.info("已随机分配完成");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupBody groupBody) throws Exception {
        // TODO Auto-generated method stub
        String groupId = groupBody.getToGroupId();
        String fileType = groupBody.getFileType();
        ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
        List<String> nameList = new ArrayList<>();
        // foreach online user
        for (Channel channel : channelGroup) {
            String user = SessionUtil.getUser(channel).getUserName();
            nameList.add(user);
        }
        User user = SessionUtil.getUser(ctx.channel());

        Lobby lobby = SessionUtil.getLobby(groupId);


        if ("80".equals(fileType)) {
            // 坐下
            Player player = new Player();
            player.setId(user.getUserId());
            player.setName(user.getUserName());
            player.setAvater(user.getAvater());
            lobby.setPlayer(player, Integer.parseInt(groupBody.getMessage()));
        }
        if ("81".equals(fileType)) {
            // 离开房间
            synchronized (this) {
                Player player = new Player();
                player.setId(user.getUserId());
                lobby.offPlayer(player);
                boolean flag = false;
                for (int i = 0; i < lobby.getPlayers().length; i++) {
                    if (lobby.getPlayers()[i] != null) {
                        // 房间存在玩家，不关闭，玩家继承owner
                        lobby.getPlayers()[i].setIsOwner(true);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    SessionUtil.dropLobby(groupId);
                }
            }
        }


        /*// 准备
        lobby.ready(user.getUserId());*/
        if ("82".equals(fileType)) {
            // 检查是否可以开始
            boolean fullPlayer = lobby.isFullPlayer();
//            if (!fullPlayer) {
            // 是否满员
            // boolean isFullReady = lobby.isFullReady();
//            }else{
            // 初始化 洗牌
            lobby.init();
            lobby.setStage(1);
//            }

            // 设置开始正式开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND,15);
            lobby.setStartTime(calendar.getTime());
        }


        if ("83".equals(fileType)) {
            // pick card
            Player player = lobby.findPlayer(user.getUserId());
            if (player.getOriganCard() == null) {
                Card card = lobby.pickUp(Integer.parseInt(groupBody.getMessage()));
                player.setOriganCard(card);
            }
            if (lobby.isAllPicked()) {

            }
        }

        if ("84".equals(fileType)) {
            lobby.overTimePick();

            log.info("已随机分配完成");
        }


        ByteBuf byteBuf = getByteBuf(ctx, groupId, groupBody.getMessage(), user, fileType, nameList);

        if (!fileType.startsWith("8")) {
            channelGroup.remove(ctx.channel());
        }

        channelGroup.writeAndFlush(new TextWebSocketFrame(byteBuf));

        GroupHistoryDO groupHistoryDO = new GroupHistoryDO();
        groupHistoryDO.setGroupId(groupId);
        groupHistoryDO.setFromId(Long.valueOf(user.getUserName()));
        groupHistoryDO.setType(1L);
        groupHistoryDO.setTime(System.currentTimeMillis());
        groupHistoryDO.setMsgId(groupBody.getMsgId());
        groupHistoryDO.setContent(groupBody.getMessage());
        groupHistoryDO.setFileType(groupBody.getFileType());

//		this.kafkaTemplate = SpringUtils.getBean(KafkaTemplate.class);
//		kafkaTemplate.send("group",  JSONObject.toJSONString(groupHistoryDO));

        channelGroup.add(ctx.channel());
    }

    public ByteBuf getByteBuf(ChannelHandlerContext ctx, String groupId, String message,
                              User fromUser, String fileType, List<String> nameList) {
        ByteBuf byteBuf = ctx.alloc().buffer();
        JSONObject data = new JSONObject();
        data.put("type", 10);
        data.put("status", 200);
        JSONObject params = new JSONObject();
        params.put("message", message);
        params.put("fileType", fileType);
        params.put("fromUser", fromUser.getUserName());
        params.put("nickName", fromUser.getNickName());
        params.put("avater", fromUser.getAvater());
        params.put("groupId", groupId);
        Collections.reverse(nameList);
        params.put("nameList", nameList);
        data.put("params", params);
        byte[] bytes = data.toJSONString().getBytes(Charset.forName("utf-8"));
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }


    public void checkMessage(String message) {


    }

    public void gameTest() {
        Card[] cards = new Card[8];

        // 2W.2C.1P.1T.1B.1I
        Card card1 = new Card();
        card1.setCardName("WOLF");
        cards[0] = card1;
        Card card12 = new Card();
        card12.setCardName("WOLF");
        cards[1] = card12;
        Card card2 = new Card();
        card2.setCardName("PROPHET");
        cards[2] = card2;
        Card card3 = new Card();
        card3.setCardName("CIVILIAN");
        cards[3] = card3;
        Card card32 = new Card();
        card32.setCardName("CIVILIAN");
        cards[4] = card32;
        Card card4 = new Card();
        card4.setCardName("TREATERS");
        cards[5] = card4;
        Card card5 = new Card();
        card5.setCardName("BANDIT");
        cards[6] = card5;
        Card card6 = new Card();
        card6.setCardName("INSOMNIA");
        cards[7] = card6;

        Operate operate = new Operate(cards);
        operate.init();
    }
}
