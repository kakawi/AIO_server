package com.hlebon.client;


import com.hlebon.message.LoginMessage;
import com.hlebon.message.Message;
import com.hlebon.message.StaticMessages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AioTcpClient {
    public static JTextField jt=new JTextField();
    public static ConcurrentHashMap<String,AsynchronousSocketChannel> sockets =new ConcurrentHashMap<>();

    static AioTcpClient me;

    private AsynchronousChannelGroup asyncChannelGroup;

    public AioTcpClient() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
    }

    private final CharsetDecoder decoder = Charset.forName("GBK").newDecoder();

    public void start(final String ip, final int port) throws Exception {
        int i = 0;
            try {
                AsynchronousSocketChannel connector = null;
                if (connector == null || !connector.isOpen()) {
                    connector = AsynchronousSocketChannel.open(asyncChannelGroup);

                    sockets.putIfAbsent(String.valueOf(i), connector);
                    connector.setOption(StandardSocketOptions.TCP_NODELAY, true);
                    connector.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    connector.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                    connector.connect(new InetSocketAddress(ip, port), connector, new AioConnectHandler(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void work() throws Exception{
        AioTcpClient client = new AioTcpClient();
        client.start("localhost", 9008);
    }

    public void send() throws UnsupportedEncodingException {

        AsynchronousSocketChannel socket=sockets.get("0");

        // 组发送buffer
//        String sendString=jt.getText();
//        ByteBuffer clientBuffer=ByteBuffer.wrap(sendString.getBytes("UTF-8"));

        // MY
        try {
            LoginMessage loginMessage = new LoginMessage();
            byte[] objectInByte = toByte(loginMessage);

            int length = objectInByte.length;
            String sendString = "sizeNextObject:" + length + StaticMessages.THE_END_FROM_CLIENT;
            ByteBuffer objectSizeBuffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
            socket.write(objectSizeBuffer);

            ByteBuffer readyBuffer = ByteBuffer.allocate(StaticMessages.READY_FROM_SERVER.length());
            socket.read(readyBuffer, readyBuffer, new AioReadReadyHandler(socket, objectInByte));
        } catch (IOException e) {
            e.printStackTrace();
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

    public   void createPanel() {
        me=this;
        JFrame f = new JFrame("Wallpaper");
        f.getContentPane().setLayout(new BorderLayout());

        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bt=new JButton("Send");
        p.add(bt);
        me=this;
        bt.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    me.send();

                } catch (Exception ex) {
                    Logger.getLogger(AioTcpClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        bt=new JButton("结束 NOTHING");
        p.add(bt);
        me=this;
        bt.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            }

        });

        f.getContentPane().add(jt,BorderLayout.CENTER);
        f.getContentPane().add(p, BorderLayout.EAST);

        f.setSize(450, 300);
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo (null);
        f.setVisible (true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AioTcpClient d = null;
                try {
                    d = new AioTcpClient();
                } catch (Exception ex) {
                    Logger.getLogger(AioTcpClient.class.getName()).log(Level.SEVERE, null, ex);
                }

                d.createPanel();
                try {
                    d.work();
                } catch (Exception ex) {
                    Logger.getLogger(AioTcpClient.class.getName()).log(Level.SEVERE, null, ex);
                }


            }
        });
    }
}
