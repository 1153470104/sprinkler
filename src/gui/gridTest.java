package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class gridTest {
    public static void main(String[] args) {
        Frame frame = new Frame("grid frame");
        frame.setLayout(null);
        frame.setBounds(200, 150, 600, 400);
        Panel panel1 = new Panel(new BorderLayout());
        Panel panel2 = new Panel(new BorderLayout());
        Panel panel3 = new Panel(new GridLayout(2, 1));
        Panel panel4 = new Panel(new GridLayout(2, 2));
        panel1.setBounds(0, 0, 600, 200);
        panel2.setBounds(0, 200, 600, 200);
        frame.add(panel1);
        frame.add(panel2);

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

        panel1.add(btn1, BorderLayout.EAST);
        panel3.add(btn2);
        panel3.add(btn3);
        panel1.add(panel3, BorderLayout.CENTER);
        panel1.add(btn4, BorderLayout.WEST);
        panel2.add(btn5, BorderLayout.EAST);
        panel4.add(btn6);
        panel4.add(btn7);
        panel4.add(btn8);
        panel4.add(btn9);
        panel2.add(panel4, BorderLayout.CENTER);
        panel2.add(btn10, BorderLayout.WEST);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
