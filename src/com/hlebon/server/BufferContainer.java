package com.hlebon.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class BufferContainer {

    private ByteBuffer buffer;

    private AsynchronousSocketChannel client;

    public BufferContainer(AsynchronousSocketChannel client,
                           ByteBuffer buffer) {
        this.buffer = buffer;
        this.client = client;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }
}