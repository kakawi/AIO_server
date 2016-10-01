package com.hlebon.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class AioReadReadyHandler implements CompletionHandler<Integer,ByteBuffer>
{
    private AsynchronousSocketChannel socket;
    private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private byte[] objectInByte;

    public AioReadReadyHandler(AsynchronousSocketChannel socket, byte[] objectInByte) {
        this.objectInByte = objectInByte;
        this.socket = socket;
    }

    //
    public void cancelled(ByteBuffer attachment) {
        System.out.println("cancelled");
    }


    @Override
    public void completed(Integer i, ByteBuffer readyBuffer) {
        if (i > 0) {
            if (readyBuffer.position() < 5) {
                socket.read(readyBuffer, readyBuffer, this);
            } else {
                try {
                    readyBuffer.flip();
                    CharBuffer readMessage = decoder.decode(readyBuffer);
                    System.out.println("Socket:/"+socket.getRemoteAddress().toString()+"/:DAS FROM SERVER = " + readMessage);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(objectInByte);
                    socket.write(byteBuffer);
                    socket.read(ByteBuffer.allocate(1024), null, new AioReadHandler(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (i == -1) {
            try {
                System.out.println("对端断线:" + socket.getRemoteAddress().toString());
                readyBuffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 读到0个字节, 不再重新发起异步读了!!!!!!!!!!!!!!
    }

    @Override
    public void failed(Throwable exc, ByteBuffer buf) {
        System.out.println(exc);
    }


}