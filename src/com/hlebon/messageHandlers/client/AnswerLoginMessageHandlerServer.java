package com.hlebon.messageHandlers.client;

import com.hlebon.client.RouteServiceClient;
import com.hlebon.client.SwingControl;
import com.hlebon.client.SenderServiceClient;
import com.hlebon.message.AnswerLoginMessage;
import com.hlebon.message.Message;

public class AnswerLoginMessageHandlerServer implements MessageHandlerClient {
    private final SenderServiceClient senderServiceClient;
    private final RouteServiceClient routeServiceClient;
    private String myName;

    public AnswerLoginMessageHandlerServer(String myName, SenderServiceClient senderServiceClient, RouteServiceClient routeServiceClient) {
        this.senderServiceClient = senderServiceClient;
        this.routeServiceClient = routeServiceClient;
        this.myName = myName;
    }

    @Override
    public void handle(Message message) {
        if (message instanceof AnswerLoginMessage) {
            AnswerLoginMessage answerLoginMessage = (AnswerLoginMessage) message;

            SwingControl swingControl = new SwingControl(myName, senderServiceClient);
            routeServiceClient.setSwingControl(swingControl);
            swingControl.connectedToChat(answerLoginMessage);
        }
    }
}
