package gui;

import javax.swing.*;
import java.awt.*;

public class displayFrame extends JFrame {

    public displayFrame() {
        setTitle("data stream display");
        Container container = this.getContentPane();
        container.setLayout(null);

        JTextArea textArea = new JTextArea(100, 20);
        JScrollPane js = new JScrollPane(textArea);
        js.setBounds(650, 0, 300, 800);
        container.add(js);

        this.setVisible(true);
        this.setBounds(100, 100, 1000, 500);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new displayFrame();
    }
}
