package com.hlebon.server;

import com.hlebon.messageHandlers.ReceivedMessageHandlerThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
    private ReceivedMessageHandlerThread receivedMessageHandlerThread;

    public AioAcceptHandler(ReceivedMessageHandlerThread receivedMessageHandlerThread) {
        this.receivedMessageHandlerThread = receivedMessageHandlerThread;
    }

    private AsynchronousSocketChannel socket;
    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel attachment) {
        try {
            System.out.println("aio.AioAcceptHandler.completed called");
            attachment.accept(attachment, this);   // attachment就是Listening Socket
            System.out.println("有客户端连接:" + socket.getRemoteAddress().toString());

            startRead(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment)
    {
        exc.printStackTrace();
    }

    public void startRead(AsynchronousSocketChannel socket) {
        ByteBuffer clientBuffer = ByteBuffer.allocate(1024);
        //read的原型是
        //read(ByteBuffer dst, A attachment, CompletionHandler<Integer,? super A> handler)
        AioReadHandler rd = new AioReadHandler(socket, receivedMessageHandlerThread);
        socket.read(clientBuffer, clientBuffer, rd);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
