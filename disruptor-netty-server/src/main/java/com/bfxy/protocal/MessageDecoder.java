package com.bfxy.protocal;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.bfxy.entity.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textFrame, List<Object> out) throws Exception {
        String json = textFrame.text();
        try {
            ChatMessage message = JSON.parseObject(json, ChatMessage.class);
            out.add(message);
        } catch (JSONException e) {
            log.error("消息: {}，格式异常", json, e);
            channelHandlerContext.writeAndFlush(new TextWebSocketFrame("消息（" + json + "）格式不正确"));
        } catch (Exception e) {
            log.error("消息序列化异常", e);
        }
    }
}
