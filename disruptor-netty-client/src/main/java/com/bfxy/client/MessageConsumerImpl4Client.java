package com.bfxy.client;

import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.entity.ChatMessage;
import com.bfxy.entity.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerImpl4Client extends MessageConsumer {

    public MessageConsumerImpl4Client(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWrapper event) throws Exception {
        ChatMessage response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //业务逻辑处理:
        try {
            System.err.println("收到" + response.getName() + "消息:" + response.getMessage());
//            ctx.writeAndFlush(event);
        } finally {
            ReferenceCountUtil.release(response);
        }
    }

}
