package com.bfxy.server;

import com.bfxy.disruptor.MessageProducer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.bfxy.entity.ChatMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @Author zhouligang
 * @Date 2023/8/3 16:25
 */
@Slf4j
@ChannelHandler.Sharable
public class SimpleServerHandler extends SimpleChannelInboundHandler<ChatMessage> {


    /**
     * 对channel进行管理
     */
    private static final ChannelGroup GROUP = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
        // 用socket.html测试
        //自已的应用服务应该有一个ID生成规则
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);

        //获取发送消息的channel
        Channel channel = ctx.channel();

        // channel加入到缓存中,这个缺点在于，目标不说话就无法收到消息.在socket5中可以用userEventTriggered处理请求，拿到token，放入缓存
        Set<Channel> channels = Group.put(msg.getRoomId(), channel);

        // 广播给所有人，如果只是两个人聊天，那么发送给另外一个人即可
        for (Channel ch : channels) {
            // 如果连接失效，则剔除
            if (!ch.isActive()) {
                Group.remove(msg.getRoomId(), ch);
                GROUP.remove(ch);
                continue;
            }
            ChatMessage message = new ChatMessage(msg);
            //代表是自己的消息
            if (ch == channel) {
                message.setName("你");
            }
            // 用socket.html测试
//            ch.writeAndFlush(message);
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
