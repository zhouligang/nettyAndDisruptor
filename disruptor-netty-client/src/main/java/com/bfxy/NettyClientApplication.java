package com.bfxy;

import com.bfxy.client.MessageConsumerImpl4Client;
import com.bfxy.disruptor.MessageConsumer;
import com.bfxy.disruptor.RingBufferWorkerPoolFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(NettyClientApplication.class, args);

        MessageConsumer[] conusmers = new MessageConsumer[4];
        for (int i = 0; i < conusmers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImpl4Client("code:clientId:" + i);
            conusmers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024 * 1024,
                //new YieldingWaitStrategy(),
                new BlockingWaitStrategy(),
                conusmers);

        //建立连接 并发送消息
//        NettyClient zhangsan = new NettyClient();
//        NettyClient lisi = new NettyClient();
//        Thread.sleep(100L);
//        zhangsan.sendData("张三", "李四,你好");
//        Thread.sleep(100L);
//        lisi.sendData("李四", "张三,你好，你现在在哪");
    }
}
