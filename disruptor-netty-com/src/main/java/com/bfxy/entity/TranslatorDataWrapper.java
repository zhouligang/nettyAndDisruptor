package com.bfxy.entity;

import io.netty.channel.ChannelHandlerContext;

public class TranslatorDataWrapper {

    private ChatMessage data;

    private ChannelHandlerContext ctx;

    public ChatMessage getData() {
        return data;
    }

    public void setData(ChatMessage data) {
        this.data = data;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

}
