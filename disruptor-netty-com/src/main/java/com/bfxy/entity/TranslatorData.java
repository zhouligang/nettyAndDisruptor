package com.bfxy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TranslatorData implements Serializable {

    private static final long serialVersionUID = 8763561286199081881L;

    /**
     * ID
     */
    private String id;

    /**
     * 发送人名称
     */
    private String name;

    /**
     * 传输消息体内容
     */
    private String message;

    /**
     * 聊天室ID
     */
    private String roomId;

    public TranslatorData() {
    }

    public TranslatorData(TranslatorData request) {
        this.id = request.getId();
        this.name = request.getName();
        this.message = request.getMessage();
        this.roomId = request.getRoomId();
    }
}
