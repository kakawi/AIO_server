package com.hlebon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
    private RouteServiceServer routeServiceServer;

    public AioAcceptHandler(RouteServiceServer routeServiceServer) {
        this.routeServiceServer = routeServiceServer;
    }

    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel attachment) {
        try {
            System.out.println("aio.AioAcceptHandler.completed called");
            attachment.accept(attachment, this);   // attachment就是Listening Socket
            System.out.println(":" + socket.getRemoteAddress().toString());

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
        ByteBuffer clientBuffer = ByteBuffer.allocate(102400);
        //read的原型是
        //read(ByteBuffer dst, A attachment, CompletionHandler<Integer,? super A> handler)
        AioReadHandlerServer rd = new AioReadHandlerServer(socket, routeServiceServer);
        socket.read(clientBuffer, clientBuffer, rd);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
