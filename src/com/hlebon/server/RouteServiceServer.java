package com.hlebon.server;


import com.hlebon.message.MessageWrapper;
import com.hlebon.messageHandlers.server.MessageHandlerServer;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class RouteServiceServer implements Runnable {
    private BlockingDeque<MessageWrapper> receivedMessageQueue = new LinkedBlockingDeque<>();
    private final Map<Class, MessageHandlerServer> messageHandlers;

    public RouteServiceServer(Map<Class, MessageHandlerServer> messageHandlers) {
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

            MessageHandlerServer messageHandlerServer = messageHandlers.get(messageWrapper.getMessage().getClass());
            messageHandlerServer.handle(messageWrapper);
        }
    }
}
