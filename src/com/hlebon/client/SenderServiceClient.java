package com.hlebon.client;


import com.hlebon.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

public class SenderServiceClient implements Runnable {
    private BlockingDeque<Message> queue = new LinkedBlockingDeque<>();
    private final AsynchronousSocketChannel serverSocket;

    public SenderServiceClient(AsynchronousSocketChannel serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void addMessageToSend(Message message) {
        queue.add(message);
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (queue.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Message message = queue.poll();

            try {
                byte[] objectInByte = toByte(message);
                int length = objectInByte.length;

                ByteBuffer byteBuffer = ByteBuffer.allocate(length + 2);
                byteBuffer.put(objectInByte);
                byteBuffer.put((byte)-1);
                byteBuffer.put((byte)-1);
                byteBuffer.rewind();

                System.out.println("Send message to Server: " + message);

                do {
                    Future<Integer> future = serverSocket.write(byteBuffer);
                    future.get();
                } while (byteBuffer.position() < byteBuffer.limit());
            } catch (IOException | InterruptedException | ExecutionException e) {
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
