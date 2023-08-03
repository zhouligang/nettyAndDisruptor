package com.bfxy.protocal;

import com.alibaba.fastjson2.JSON;
import com.bfxy.entity.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class MessageEncoder extends MessageToMessageEncoder<ChatMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ChatMessage msg, List<Object> out) {
        String json = JSON.toJSONString(msg);
        TextWebSocketFrame frame = new TextWebSocketFrame(json);
        out.add(frame);
    }
}