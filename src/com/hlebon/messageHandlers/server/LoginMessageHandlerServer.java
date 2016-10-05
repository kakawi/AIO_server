package com.hlebon.messageHandlers.server;

import com.hlebon.message.LoginMessage;
import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.server.SenderServiceServer;

public class LoginMessageHandlerServer implements MessageHandlerServer {
    private SenderServiceServer senderServiceServer;

    public LoginMessageHandlerServer(SenderServiceServer senderServiceServer) {
        this.senderServiceServer = senderServiceServer;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        Message message = messageWrapper.getMessage();
        if (message instanceof LoginMessage) {
            LoginMessage loginMessage = (LoginMessage) message;
            senderServiceServer.addClient(loginMessage.getName(), messageWrapper.getFrom());
            System.out.println("We have a new client: " + loginMessage.getName());
        }
    }
}
