package com.hlebon.server;

import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;
import com.hlebon.messageHandlers.ReceivedMessageHandlerThread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

public class AioReadHandler implements CompletionHandler<Integer,ByteBuffer> {
    private ReceivedMessageHandlerThread receivedMessageHandlerThread;
    private AsynchronousSocketChannel socket;
    private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private List<Byte> poolByte = new ArrayList<>();
    private int counter;

    public AioReadHandler(AsynchronousSocketChannel socket, ReceivedMessageHandlerThread receivedMessageHandlerThread) {
        this.receivedMessageHandlerThread = receivedMessageHandlerThread;
        this.socket = socket;
    }

    @Override
    public void completed(Integer i, ByteBuffer buffer) {
        if (i > 0) {
            buffer.flip();
            for (int j = 0; j < buffer.limit(); j++) {
                byte currentByte = buffer.get(j);
                if (currentByte == -1 && poolByte.get(poolByte.size() - 1) == -1) {
                    System.out.println(counter++);
                    poolByte.remove(poolByte.size() - 1);
                    byte[] messageFromByte = new byte[poolByte.size()];
                    for (int k = 0; k < poolByte.size(); k++) {
                        messageFromByte[k] = poolByte.get(k);
                    }

                    try {
                        Object object = toObject(messageFromByte);
                        if (object instanceof Message) {
                            Message message = (Message) object;
                            MessageWrapper messageWrapper = new MessageWrapper(message, socket);
                            receivedMessageHandlerThread.addMessageToHandle(messageWrapper);
                            System.out.println(message);
                        }
                        poolByte.clear();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    poolByte.add(currentByte);
                }
            }
            buffer.clear();
            socket.read(buffer, buffer, this);
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

    private static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in;

        in = new ObjectInputStream(bis);
        Object o = in.readObject();
        return o;
    }

}
