package com.bfxy.server;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.ChatMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 对channel进行管理
     */
    private static final ChannelGroup GROUP = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 用client测试
        ChatMessage request = (ChatMessage) msg;
        //自已的应用服务应该有一个ID生成规则
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);

        //获取发送消息的channel
        Channel channel = ctx.channel();

        // channel加入到map中
        Set<Channel> channels = Group.put(request.getRoomId(), channel);

        // 广播给所有人，如果只是两个人聊天，那么发送给另外一个人即可
        for (Channel ch : channels) {
            // 如果连接失效，则剔除
            if (!ch.isActive()) {
                Group.remove(request.getRoomId(), ch);
                GROUP.remove(ch);
                continue;
            }
            ChatMessage message = new ChatMessage(request);
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
        log.error("连接发生异常", cause);
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
            messageProducer.onData(new ChatMessage().setMessage(message).setName("系统"), ctx);
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
            messageProducer.onData(new ChatMessage().setMessage(message), ctx);
        }
        GROUP.remove(channel);
    }
}
