package hsailtest.common;

/**
 * Created with IntelliJ IDEA.
 * User: garyfrost
 * Date: 10/27/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
//http://www.coderanch.com/t/342909/GUI/java/Quick-drawing-LED-style-text

import javax.swing.*;
import java.awt.*;

public class Watch extends LEDWidget {

    private long startTime = 0L;
    private long stopTime = 0L;
    private boolean running = false;

    private Timer timer = new Timer(97, (e) -> {
        stopTime = System.currentTimeMillis();
        setMilliValue((int) (stopTime - startTime));
    });

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
        setColor(Color.RED);
        timer.start();
    }

    public void stop() {
        timer.stop();
        stopTime = System.currentTimeMillis();
        setMilliValue((int) (stopTime - startTime));
        setColor(Color.GREEN);
    }

    public Watch() {
        super();
        setColor(Color.GREEN);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Stopwatch");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Watch w = new Watch();

            f.getContentPane().add(w.getContainer(), BorderLayout.CENTER);
            JButton start = new JButton("start");
            start.addActionListener(ev -> w.start());
            JButton stop = new JButton("stop");
            stop.addActionListener(ev -> w.stop());
            JPanel panel = new JPanel();
            panel.add(start);
            panel.add(stop);
            f.getContentPane().add(panel, BorderLayout.NORTH);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });


    }
}

