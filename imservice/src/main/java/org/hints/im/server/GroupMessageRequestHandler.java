package org.hints.im.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.hints.im.pojo.GroupBody;
import org.hints.im.pojo.entity.GroupHistoryDO;
import org.hints.im.utils.SessionUtil;
import org.hints.im.utils.SpringUtils;
import org.nutz.dao.Dao;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 发送群消息handler组件
 * @author holiday
 * 2020-11-16
 */
@Sharable
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupBody> {

	private KafkaTemplate kafkaTemplate;
	private Dao dao;


	public static GroupMessageRequestHandler INSTANCE = new GroupMessageRequestHandler();
	
	private GroupMessageRequestHandler() {
		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GroupBody groupBody) throws Exception {
		// TODO Auto-generated method stub
		String groupId = groupBody.getToGroupId();
		String fileType = groupBody.getFileType();
		ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
		List<String> nameList = new ArrayList<>();

		for (Channel channel : channelGroup) {
			String user = SessionUtil.getUser(channel).getUserName();
			nameList.add(user);
		}
		String user = SessionUtil.getUser(ctx.channel()).getUserName();
		ByteBuf byteBuf = getByteBuf(ctx, groupId, groupBody.getMessage(), user, fileType, nameList);
		channelGroup.remove(ctx.channel());
		channelGroup.writeAndFlush(new TextWebSocketFrame(byteBuf));

		GroupHistoryDO groupHistoryDO = new GroupHistoryDO();
		groupHistoryDO.setGroupId(groupId);
		groupHistoryDO.setFromId(Long.valueOf(user));
		groupHistoryDO.setType(1L);
		groupHistoryDO.setTime(System.currentTimeMillis());
		groupHistoryDO.setMsgId(groupBody.getMsgId());
		groupHistoryDO.setContent(groupBody.getMessage());
		groupHistoryDO.setFileType(groupBody.getFileType());

		this.kafkaTemplate = SpringUtils.getBean(KafkaTemplate.class);
		kafkaTemplate.send("group",  JSONObject.toJSONString(groupHistoryDO));

		channelGroup.add(ctx.channel());
	}
	
	public ByteBuf getByteBuf(ChannelHandlerContext ctx, String groupId, String message,
                              String fromUser, String fileType, List<String> nameList) {
		ByteBuf byteBuf = ctx.alloc().buffer();
		JSONObject data = new JSONObject();
		data.put("type", 10);
		data.put("status", 200);
		JSONObject params = new JSONObject();
		params.put("message", message);
		params.put("fileType", fileType);
		params.put("fromUser", fromUser);
		params.put("groupId", groupId);
		Collections.reverse(nameList);
		params.put("nameList", nameList);
		data.put("params", params);
		byte []bytes = data.toJSONString().getBytes(Charset.forName("utf-8"));
		byteBuf.writeBytes(bytes);
		return byteBuf;
	}
}
