package com.hlebon.client;

import com.hlebon.message.AnswerLoginMessage;
import com.hlebon.message.LogoutMessageClient;
import com.hlebon.message.NewClientMessage;
import com.hlebon.message.SayMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SwingControl implements Runnable {

    private SenderServiceClient senderServiceClient;
    private final String myName;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JTextField textField;
    private JTextArea textArea;
    private JComboBox<String> comboBox;
    private String textChat;

    public SwingControl(String myName, SenderServiceClient senderServiceClient) {
        this.myName = myName;
        this.senderServiceClient = senderServiceClient;
        prepareGUI();
        showEventDemo();
    }

    public void connectedToChat(AnswerLoginMessage answerLoginMessage) {
        textArea.append("We're in the chat");
        ArrayList<String> existedClients = answerLoginMessage.getExistedClients();
        for (String existedClient: existedClients) {
            comboBox.addItem(existedClient);
        }
    }

    public void newClient(NewClientMessage newClientMessage) {
        String nameNewClient = newClientMessage.getName();
        textArea.append("\nNew client has come: " + nameNewClient);
        comboBox.addItem(nameNewClient);
    }

    public void logoutClient(LogoutMessageClient logoutMessageClient) {
        String nameClient = logoutMessageClient.getNameClient();
        textArea.append("\nClient " + nameClient + " logout");
        comboBox.removeItem(nameClient);
    }

    public void addMessage(SayMessage sayMessage) {
        String from = sayMessage.getFrom();
        String text = sayMessage.getText();
        textArea.append("\n[" + from + "] " + text);
    }

    private void sendMessage(SayMessage sayMessage) {
        senderServiceClient.addMessageToSend(sayMessage);
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Chat :: " + myName);
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(3, 1));

        headerLabel = new JLabel("",JLabel.CENTER );
        statusLabel = new JLabel("",JLabel.CENTER);

        statusLabel.setSize(350,100);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        comboBox = new JComboBox<>();
        topPanel.add(comboBox);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        textField = new JTextField(14);
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(new ButtonClickListener());
        bottomPanel.add(textField);
        bottomPanel.add(okButton);

        mainFrame.add(topPanel);
        mainFrame.add(controlPanel);

        mainFrame.add(bottomPanel);
        mainFrame.setVisible(true);
    }

    private void showEventDemo(){
        textArea = new JTextArea(10,20);
        controlPanel.add(textArea);

        mainFrame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if(command.equals( "OK" ))  {
                String nameDestinationClient = (String)comboBox.getSelectedItem();
                String text = textField.getText();
                textField.setText("");
                SayMessage sayMessage = new SayMessage(myName, nameDestinationClient, text);
                senderServiceClient.addMessageToSend(sayMessage);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            System.out.print("Enter the Client Name: ");
            try {
                String clientName = bufferedReader.readLine();
                System.out.println(clientName);
                System.out.print("Enter the message: ");
                String text = bufferedReader.readLine();
                SayMessage sayMessage = new SayMessage(myName, clientName, text);
                sendMessage(sayMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
