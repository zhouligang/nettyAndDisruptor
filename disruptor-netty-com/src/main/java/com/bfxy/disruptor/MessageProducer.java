package com.bfxy.disruptor;

import com.bfxy.entity.ChatMessage;
import com.bfxy.entity.TranslatorDataWrapper;
import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelHandlerContext;

public class MessageProducer {

    private String producerId;

    private RingBuffer<TranslatorDataWrapper> ringBuffer;

    public MessageProducer(String producerId, RingBuffer<TranslatorDataWrapper> ringBuffer) {
        this.producerId = producerId;
        this.ringBuffer = ringBuffer;
    }

    public void onData(ChatMessage data, ChannelHandlerContext ctx) {
        long sequence = ringBuffer.next();
        try {
            TranslatorDataWrapper wapper = ringBuffer.get(sequence);
            wapper.setData(data);
            wapper.setCtx(ctx);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
