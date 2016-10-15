package com.hlebon.messageHandlers.server;

import com.hlebon.server.SenderServiceServer;
import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.message.SayMessage;

public class SayMessageHandlerServer implements MessageHandlerServer {
    private SenderServiceServer senderServiceServer;

    public SayMessageHandlerServer(SenderServiceServer senderServiceServer) {
        this.senderServiceServer = senderServiceServer;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        Message message = messageWrapper.getMessage();
        if (message instanceof SayMessage) {
            SayMessage sayMessage = (SayMessage) message;
            senderServiceServer.sendMessageByName(sayMessage.getTo(), sayMessage);
        }
    }
}
