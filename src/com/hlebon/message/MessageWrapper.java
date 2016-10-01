package com.hlebon.message;

import java.nio.channels.AsynchronousSocketChannel;

public class MessageWrapper extends Message {
    private Message message;
    private AsynchronousSocketChannel clientSocket;

    public MessageWrapper(Message message, AsynchronousSocketChannel clientSocket) {
        this.message = message;
        this.clientSocket = clientSocket;
    }

    public Message getMessage() {
        return message;
    }

    public AsynchronousSocketChannel getClientSocket() {
        return clientSocket;
    }
}
