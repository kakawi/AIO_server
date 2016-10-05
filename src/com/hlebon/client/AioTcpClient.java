package com.hlebon.client;


import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AioTcpClient {
    AsynchronousChannelGroup asyncChannelGroup;

    public AioTcpClient() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
    }

    public void start(final String ip, final int port) throws Exception {
        AsynchronousSocketChannel serverSocket = AsynchronousSocketChannel.open(asyncChannelGroup);

        serverSocket.setOption(StandardSocketOptions.TCP_NODELAY, true);
        serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        serverSocket.connect(new InetSocketAddress(ip, port), serverSocket, new AioConnectHandler());
        Thread.sleep(400000);
    }

    public static void main(String[] args) throws Exception {
        AioTcpClient client = new AioTcpClient();
        client.start("localhost", 9008);
    }
}
