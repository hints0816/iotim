package org.hints.im.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/5 17:28
 */
public class SessionUtil {
    /**
     * userID 映射 连接channel
     */
    private static Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    /**
     * groupId ---> channelgroup 群聊ID和群聊ChannelGroup映射
     */
    private static Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<>();

    public static void bindChannel(String username, Channel channel) {
        userIdChannelMap.put(username, channel);
        channel.attr(Attributes.SESSION).set(username);
    }

    public static void unbind(Channel channel) {
        if (hasLogin(channel)) {
            userIdChannelMap.remove(getUser(channel));
            channel.attr(Attributes.SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }

    public static String getUser(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    public static Channel getChannel(String userId) {
        return userIdChannelMap.get(userId);
    }
    /**
     * 绑定群聊Id  群聊channelGroup
     * @param groupId
     * @param channelGroup
     */
    public static void bindChannelGroup(String groupId, ChannelGroup channelGroup) {
        groupIdChannelGroupMap.put(groupId, channelGroup);
    }

    public static ChannelGroup getChannelGroup(String groupId) {
        return groupIdChannelGroupMap.get(groupId);
    }
}

