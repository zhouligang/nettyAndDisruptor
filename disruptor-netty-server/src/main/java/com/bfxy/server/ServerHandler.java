package com.bfxy.server;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.TranslatorData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 对channel进行管理
     */
    private static final ChannelGroup GROUP = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TranslatorData request = (TranslatorData) msg;
        //自已的应用服务应该有一个ID生成规则
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        //获取发送消息的channel
        Channel channel = ctx.channel();
        for (Channel ch : GROUP) {
            TranslatorData message = new TranslatorData(request);
            //代表是自己的消息
            if (ch == channel) {
                message.setName("你");
            }
            messageProducer.onData(message, ctx);
        }
    }

    /**
     * 上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[" + channel.remoteAddress() + "]" + "上线");
    }

    /**
     * 下线
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[" + channel.remoteAddress() + "]" + "下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(
                "[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.close().sync();
    }

    /**
     * 当有新的连接的时候进行通知
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        String message;
        for (Channel ch : GROUP) {
            message = "[" + ch.remoteAddress() + "] " + "上线了";
            messageProducer.onData(new TranslatorData().setMessage(message).setName("系统"), ctx);
        }
        GROUP.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        String message;
        for (Channel ch : GROUP) {
            message = "[" + ch.remoteAddress() + "] " + "下线了";
            messageProducer.onData(new TranslatorData().setMessage(message), ctx);
        }
        GROUP.remove(channel);
    }
}
