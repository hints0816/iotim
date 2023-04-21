package org.hints.im.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.hints.game.Lobby;
import org.hints.im.pojo.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/5 17:28
 */
public class SessionUtil {
    /**
     * userID 映射 连接channel
     */
    private static Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    /**
     * groupId --->
     */
    private static Map<String, Lobby> groupIdLobbyMap = new ConcurrentHashMap<>();

    /**
     * groupId ---> channelgroup 群聊ID和群聊ChannelGroup映射
     */
    private static Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<>();

    public static void bindChannel(User user, Channel channel) {
        userIdChannelMap.put(user.getUserName(), channel);
        channel.attr(Attributes.SESSION).set(user);
    }

    public static void unbind(Channel channel) {
        if (hasLogin(channel)) {
            userIdChannelMap.remove(getUser(channel).getUserName());
            channel.attr(Attributes.SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }

    public static User getUser(Channel channel) {
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

    public static void bindLobby(String groupId, Lobby lobby) {
        groupIdLobbyMap.put(groupId, lobby);
    }

    public static Lobby getLobby(String groupId) {
        return groupIdLobbyMap.get(groupId);
    }

}

