package com.hlebon.server;

import AIO.message.StaticMessages;
import AIO.messageHandlers.ReceivedMessageHandlerThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class AioReadHandler implements CompletionHandler<Integer,ByteBuffer> {
    private ReceivedMessageHandlerThread receivedMessageHandlerThread;
    private AsynchronousSocketChannel socket;
    private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    public  String msg;
    StringBuilder stringBuilder = new StringBuilder();

    public AioReadHandler(AsynchronousSocketChannel socket, ReceivedMessageHandlerThread receivedMessageHandlerThread) {
        this.receivedMessageHandlerThread = receivedMessageHandlerThread;
        this.socket = socket;
    }

    @Override
    public void completed(Integer i, ByteBuffer buffer) {
        if (i > 0) {
            buffer.flip();
            try {
                msg = decoder.decode(buffer).toString();
                System.out.println("Socket:/" + socket.getRemoteAddress().toString() + "/:FROM CLIENT " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (msg.contains(StaticMessages.THE_END_FROM_CLIENT)) {
                int length = Integer.parseInt(msg.split(":")[1]);
                ByteBuffer readyBuffer = ByteBuffer.wrap(StaticMessages.READY_FROM_SERVER.getBytes());
                socket.write(readyBuffer);
                buffer.clear();
                BufferContainer bufferContainer = new BufferContainer(socket, ByteBuffer.allocate(length));
                socket.read(bufferContainer.getBuffer(), bufferContainer, new AioObjectHandler(socket, receivedMessageHandlerThread, length));
            } else {
                socket.read(buffer, buffer, this);
            }



//            if(stringBuilder.toString().contains("BBYYEE")) {
//                // 将读到的数据echo回客户端
//                try {
//                buf.rewind();
//                String sendString="服务器回应,你输出的是:"+stringBuilder.toString();
//                stringBuilder.setLength(0);
//                ByteBuffer clientBuffer=ByteBuffer.wrap(sendString.getBytes("UTF-8"));
//                clientBuffer.put(ByteBuffer.wrap(sendString.getBytes("UTF-8")));
//                clientBuffer.rewind();
//                // 发起异步写
//                // 第一个参数为写的buffer
//                // 第二个参数为attachment
//                // 第三个参数为CompletionHandler,
//                System.out.println("SLEEP 2s");
//                Thread.sleep(2000);
//                socket.write(clientBuffer, clientBuffer, new AioWriteHandler(socket));
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(AioReadHandler.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

        }
        else if (i == -1) {
            try {
                System.out.println("Close:" + socket.getRemoteAddress().toString());
                buffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("cancelled");
    }

}
