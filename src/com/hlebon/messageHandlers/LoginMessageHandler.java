package com.hlebon.messageHandlers;


import com.hlebon.message.LoginMessage;
import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.server.SenderService;

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
