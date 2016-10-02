package com.hlebon.messageHandlers;


import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.message.SayMessage;
import com.hlebon.server.SenderService;

public class SayMessageHandler implements MessageHandler {
    private SenderService senderService;

    public SayMessageHandler(SenderService senderService) {
        this.senderService = senderService;
    }

    @Override
    public void handle(MessageWrapper messageWrapper) {
        Message message = messageWrapper.getMessage();
        if (message instanceof SayMessage) {
            SayMessage sayMessage = (SayMessage) message;
            System.out.println(sayMessage.getC());

//            senderService.addMessageToSend(messageWrapper);
        }
    }
}
