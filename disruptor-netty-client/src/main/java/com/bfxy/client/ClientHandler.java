package com.bfxy.client;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        /**
         try {
         TranslatorData response = (TranslatorData)msg;
         System.err.println("Client端: id= " + response.getId()
         + ", name= " + response.getName()
         + ", message= " + response.getMessage());
         } finally {
         //一定要注意 用完了缓存 要进行释放
         ReferenceCountUtil.release(msg);
         }
         */
        ChatMessage response = (ChatMessage) msg;
        String producerId = "code:seesionId:002";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.onData(response, ctx);


    }
}
