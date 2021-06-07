package gui;

import client.guiClient;
import client.multiTreeClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * the display frame of gui
 */
public class displayFrame extends JFrame {
    private guiClient client;

    public displayFrame() {
        setTitle("data stream display");
        Container container = this.getContentPane();
        container.setLayout(null);

        //text area
        JTextArea outputArea = new JTextArea(100, 30);
//        JTextArea inputArea = new JTextArea(5, 30);
        JTextField inputArea = new JTextField(30);
//        inputArea.addActionListener();
        JTextArea statusArea = new JTextArea(100, 100);
        JTextArea dataArea = new JTextArea(100, 30);
        JScrollPane js = new JScrollPane(outputArea);

        //label & button
        JLabel statusLabel = new JLabel("system status");
        JLabel queryLabel = new JLabel("query statement");
        JLabel insertLabel = new JLabel("insert data");
        JLabel resultLabel = new JLabel("query result");
//        JButton queryButton = new JButton("query");
//        queryButton.addActionListener(client.queryServer);
//        queryButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String querySentence = inputArea.getText();
//            }
//        });
        JButton startButton = new JButton("start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.startSystem();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton endButton = new JButton("end");
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  //直接退出
            }
        });

        //bound setting
        insertLabel.setBounds(640, 5, 400, 20);
        dataArea.setBounds(640, 30, 400, 700);
        startButton.setBounds(140, 5, 200, 30);
        endButton.setBounds(360, 5, 200, 30);
        statusLabel.setBounds(10, 15, 600, 20);
        statusArea.setBounds(10, 45, 600, 660);
        queryLabel.setBounds(1070, 5, 400, 20);
        inputArea.setBounds(1070, 30, 400, 60);
        resultLabel.setBounds(1070, 120, 100, 20);
//        queryButton.setBounds(1170, 100, 300, 30);
        js.setBounds(1070, 150, 400, 650);

        //add all
        container.add(statusLabel);
        container.add(queryLabel);
        container.add(insertLabel);
        container.add(resultLabel);
//        container.add(queryButton);
        container.add(startButton);
        container.add(endButton);

        container.add(js);
        container.add(inputArea);
        container.add(statusArea);
        container.add(dataArea);

        this.setVisible(true);
        this.setBounds(30, 30, 1500, 730);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client = new guiClient(outputArea, statusArea, dataArea);
        inputArea.addActionListener(client.queryServer);
    }

    public static void main(String[] args) {
        new displayFrame();
    }
}
