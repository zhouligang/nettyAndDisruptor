package com.bfxy.server;

import io.netty.channel.Channel;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhouligang
 * @Date 2023/8/2 17:53
 */
public class Group {

    /**
     * key为聊天群ID
     */
    public static final Map<String, Set<Channel>> CHANNELS = new ConcurrentHashMap<>();


    public static Set<Channel> put(String roomId, Channel channel) {
        Set<Channel> channels = Group.CHANNELS.get(roomId);

        if (CollectionUtils.isEmpty(channels)) {
            channels = new LinkedHashSet<>();
            Group.CHANNELS.put(roomId, channels);
        }
        channels.add(channel);
        return channels;
    }

    public static void remove(String roomId, Channel ch) {
        Group.CHANNELS.computeIfPresent(roomId, (k, v) -> {
            v.remove(ch);
            return v;
        });
    }
}
