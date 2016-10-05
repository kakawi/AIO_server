package com.hlebon.messageHandlers.server;

import com.hlebon.message.LogoutMessageServer;
import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.server.SenderServiceServer;

import java.nio.channels.AsynchronousSocketChannel;

public class LogoutMessageHandlerServer implements MessageHandlerServer {
    private SenderServiceServer senderServiceServer;

    public LogoutMessageHandlerServer(SenderServiceServer senderServiceServer) {
        this.senderServiceServer = senderServiceServer;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        Message message = messageWrapper.getMessage();
        if (message instanceof LogoutMessageServer) {
            AsynchronousSocketChannel socketClient = messageWrapper.getFrom();
            senderServiceServer.removeClient(socketClient);
        }
    }
}
