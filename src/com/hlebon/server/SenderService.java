package com.hlebon.server;


import com.hlebon.message.Message;
import com.hlebon.message.MessageWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SenderService implements Runnable {
    private BlockingDeque<MessageWrapper> queue = new LinkedBlockingDeque<>();

    public void addMessageToSend(MessageWrapper messageWrapper) {
        queue.add(messageWrapper);
        synchronized (this) {
            notify();
        }
    }
    @Override
    public void run() {
        while (true) {
            if (queue.isEmpty()) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            MessageWrapper messageWrapper = queue.poll();
            Message message = messageWrapper.getMessage();
            AsynchronousSocketChannel socket = messageWrapper.getClientSocket();

            try {
                byte[] objectInByte = toByte(message);
                int length = objectInByte.length;
                ByteBuffer byteBuffer = ByteBuffer.allocate(length + 1);
                byteBuffer.put(objectInByte);
                byteBuffer.put((byte)'\r');
                socket.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] toByte(Message loginMessage) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        out = new ObjectOutputStream(bos);
        out.writeObject(loginMessage);
        out.flush();
        return bos.toByteArray();
    }
}
