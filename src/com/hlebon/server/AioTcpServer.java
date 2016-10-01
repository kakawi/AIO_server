package com.hlebon.server;

import AIO.message.LoginMessage;
import AIO.messageHandlers.LoginMessageHandler;
import AIO.messageHandlers.MessageHandler;
import AIO.messageHandlers.ReceivedMessageHandlerThread;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AioTcpServer implements Runnable {
    private AsynchronousChannelGroup asyncChannelGroup;
    private AsynchronousServerSocketChannel listener;
    ReceivedMessageHandlerThread receivedMessageHandlerThread;


    public AioTcpServer(int port, ReceivedMessageHandlerThread receivedMessageHandlerThread) throws Exception {
        this.receivedMessageHandlerThread = receivedMessageHandlerThread;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        listener = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(new InetSocketAddress(port));
    }

    public void run() {
        try {
            AioAcceptHandler acceptHandler = new AioAcceptHandler(receivedMessageHandlerThread);
            listener.accept(listener, acceptHandler);
            Thread.sleep(400000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("finished server");
        }
    }

    public static void main(String... args) throws Exception {
        SenderService senderService = new SenderService();
        new Thread(senderService).start();

        Map<Class, MessageHandler> messageHandlers = new HashMap<>();
        messageHandlers.put(LoginMessage.class, new LoginMessageHandler(senderService));

        ReceivedMessageHandlerThread receivedMessageHandlerThread = new ReceivedMessageHandlerThread(messageHandlers);
        new Thread(receivedMessageHandlerThread).start();

        AioTcpServer server = new AioTcpServer(9008, receivedMessageHandlerThread);
        new Thread(server).start();
    }
}
