package com.hlebon.message;

import java.io.*;

public abstract class Message implements Serializable {
    public byte[] toByte() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(this);
        return bos.toByteArray();
    }
}
