package com.bfxy.server;

import com.bfxy.protocal.MessageDecoder;
import com.bfxy.protocal.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer {

    public NettyServer() {
        //1. 创建两个工作线程组: 一个用于接受网络请求的线程组. 另一个用于实际处理业务的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        //2 辅助类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {

            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //表示缓存区动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    //缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new IdleStateHandler(60, 0, 0));
//                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder()); //client和server都用的时候
//                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder()); //client和server都用的时候
                            sc.pipeline().addLast(new HttpServerCodec());
                            sc.pipeline().addLast(new HttpObjectAggregator(65536));
                            sc.pipeline().addLast(new WebSocketServerProtocolHandler("/chat"));
                            sc.pipeline().addLast(new MessageEncoder()); //只用server时
                            sc.pipeline().addLast(new MessageDecoder()); //只用server时
                            sc.pipeline().addLast(new SimpleServerHandler());
                        }
                    });
            //绑定端口，同步等等请求连接
            ChannelFuture cf = serverBootstrap.bind(8765).sync();
            System.err.println("Server Startup...");
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅停机
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.err.println("Sever ShutDown...");
        }
    }

}
