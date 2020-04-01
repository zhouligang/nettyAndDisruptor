package com.bfxy.client;

import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.entity.TranslatorData;
import com.bfxy.entity.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerImpl4Client extends MessageConsumer {

    public MessageConsumerImpl4Client(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWrapper event) throws Exception {
        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //业务逻辑处理:
        try {
            System.err.println(response.getName() + "说:" + response.getMessage());
        } finally {
            ReferenceCountUtil.release(response);
        }
    }

}
