package com.hlebon.messageHandlers;

import AIO.message.LoginMessage;
import AIO.message.Message;
import AIO.message.MessageWrapper;
import AIO.server.SenderService;

public class LoginMessageHandler implements MessageHandler {
    private SenderService senderService;

    public LoginMessageHandler(SenderService senderService) {
        this.senderService = senderService;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        Message message = messageWrapper.getMessage();
        if (message instanceof LoginMessage) {
            LoginMessage loginMessage = (LoginMessage) message;
            System.out.println(loginMessage.getParameter());

            senderService.addMessageToSend(messageWrapper);
        }
    }
}
