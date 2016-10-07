package com.hlebon;

import com.hlebon.message.Message;

import java.io.*;

public class UtilsMethods {
    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in;

        in = new ObjectInputStream(bis);
        Object o = in.readObject();
        return o;
    }

    public static byte[] toByte(Message loginMessage) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        out = new ObjectOutputStream(bos);
        out.writeObject(loginMessage);
        out.flush();
        return bos.toByteArray();
    }
}
