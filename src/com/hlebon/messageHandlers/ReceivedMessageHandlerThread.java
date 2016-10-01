package com.hlebon.messageHandlers;


import com.hlebon.message.MessageWrapper;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ReceivedMessageHandlerThread implements Runnable {
    private BlockingDeque<MessageWrapper> receivedMessageQueue = new LinkedBlockingDeque<>();
    private Map<Class, MessageHandler> messageHandlers;

    public ReceivedMessageHandlerThread(Map<Class, MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    public void addMessageToHandle(MessageWrapper messageWrapper) {
        receivedMessageQueue.add(messageWrapper);
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            if(receivedMessageQueue.isEmpty()) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            MessageWrapper messageWrapper = receivedMessageQueue.poll();

            MessageHandler messageHandler = messageHandlers.get(messageWrapper.getMessage().getClass());
            messageHandler.handle(messageWrapper);
        }
    }
}
