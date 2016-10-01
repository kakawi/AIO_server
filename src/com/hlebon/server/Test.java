package com.hlebon.server;


import com.hlebon.message.LoginMessage;

import java.io.*;

public class Test {
    public static void main(String[] args) throws Exception{
        LoginMessage loginMessage = new LoginMessage();

        byte[] bytes = toByte(loginMessage);

        toObject(bytes);
    }

    private static byte[] toByte(LoginMessage loginMessage) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        out = new ObjectOutputStream(bos);
        out.writeObject(loginMessage);
        out.flush();
        return bos.toByteArray();
    }

    private static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;

        in = new ObjectInputStream(bis);
        Object o = in.readObject();
        return o;
    }
}
