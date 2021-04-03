package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class gridTest2 {
    public static void main(String[] args) {
        Frame frame = new Frame("grid frame");
        frame.setLayout(new GridLayout(2, 3));
        frame.setBounds(200, 150, 600, 400);
        Panel panel1 = new Panel();
        Panel panel2 = new Panel(new GridLayout(2,1));
        Panel panel3 = new Panel();
        Panel panel4 = new Panel();
        Panel panel5 = new Panel(new GridLayout(2,2));
        Panel panel6 = new Panel();
        panel1.setBounds(0, 0, 100, 200);
        panel2.setBounds(100, 0, 400, 200);
        panel3.setBounds(500, 0, 100, 200);
        panel4.setBounds(0, 200, 100, 200);
        panel5.setBounds(100, 200, 400, 200);
        panel6.setBounds(500, 200, 100, 200);
        frame.add(panel1);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);

        Button btn1 = new Button("btn1");
        Button btn2 = new Button("btn2");
        Button btn3 = new Button("btn3");
        Button btn4 = new Button("btn4");
        Button btn5 = new Button("btn5");
        Button btn6 = new Button("btn6");
        Button btn7 = new Button("btn7");
        Button btn8 = new Button("btn8");
        Button btn9 = new Button("btn9");
        Button btn10 = new Button("btn10");

        panel1.add(btn1);
        panel2.add(btn2);
        panel2.add(btn3);
        panel3.add(btn4);
        panel4.add(btn5);
        panel5.add(btn6);
        panel5.add(btn7);
        panel5.add(btn8);
        panel5.add(btn9);
        panel6.add(btn10);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
