package com.bfxy.disruptor;

import com.bfxy.entity.TranslatorDataWrapper;
import com.lmax.disruptor.WorkHandler;

/**
 * @author Alienware
 */
public abstract class MessageConsumer implements WorkHandler<TranslatorDataWrapper> {

    protected String consumerId;

    public MessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }


}
