package com.bfxy.controller;

import com.bfxy.client.NettyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 周黎钢
 * @date 2020/4/1 10:06
 * @description
 */
@RestController
@RequestMapping("/api")
public class ClientController {

    private Map<String, NettyClient> clients = new ConcurrentHashMap<>();

    @RequestMapping("/sendMsg")
    public void sendMessage(@RequestParam(value = "msg") String msg, @RequestParam(value = "userName") String userName, @RequestParam(value = "roomId") String roomId) {
        if (clients.get(userName) == null) {
            NettyClient client = new NettyClient();
            clients.put(userName, client);
        }
        NettyClient client = clients.get(userName);
        client.sendData(userName, msg, roomId);
    }

}
