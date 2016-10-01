package com.hlebon.server;

import AIO.message.Message;
import AIO.message.MessageWrapper;
import AIO.messageHandlers.ReceivedMessageHandlerThread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioObjectHandler implements CompletionHandler<Integer, BufferContainer> {
    private AsynchronousSocketChannel socket;
    private int sizeOfObjectInBytes;
    private ReceivedMessageHandlerThread receivedMessageHandlerThread;

    public AioObjectHandler(AsynchronousSocketChannel socket, ReceivedMessageHandlerThread receivedMessageHandlerThread, int sizeOfObjectInBytes) {
        this.receivedMessageHandlerThread = receivedMessageHandlerThread;
        this.socket = socket;
        this.sizeOfObjectInBytes = sizeOfObjectInBytes;
    }

    @Override
    public void completed(Integer i, BufferContainer bufferContainer) {
        if (i > 0) {
            if (bufferContainer.getBuffer().position() < sizeOfObjectInBytes) {
                socket.read(bufferContainer.getBuffer(), bufferContainer, this);
                return;
            }

            bufferContainer.getBuffer().flip();

            try {
                Object object = toObject(bufferContainer.getBuffer().array());
                if (object instanceof Message) {
                    Message message = (Message) object;
                    MessageWrapper messageWrapper = new MessageWrapper(message, socket);
                    receivedMessageHandlerThread.addMessageToHandle(messageWrapper);
                    System.out.println(message);

//                    AioReadHandler rd = new AioReadHandler(socket, receivedMessageHandlerThread);
//                    bufferContainer.getBuffer().clear();
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//                    socket.read(byteBuffer, byteBuffer, rd);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (i == -1) {
            try {
                System.out.println("Close:" + socket.getRemoteAddress().toString());
                bufferContainer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;

        in = new ObjectInputStream(bis);
        Object o = in.readObject();
        return o;
    }

    @Override
    public void failed(Throwable exc, BufferContainer attachment) {
        System.out.println("cancelled");
    }

}
