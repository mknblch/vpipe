package de.mknblch.vpipe.helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class Viewer extends JPanel {

    private final Supplier<BufferedImage> source;
    private BufferedImage image;
    private volatile boolean running = false;

    public Viewer(Supplier<BufferedImage> supplier) {
        this.source = supplier;
    }

    public void start() {
        new Thread(() -> {
            running = true;
            while (running) {
                image = source.get();
                this.repaint(20);
            }
        }).start();
    }

    public void stop() {
        running = false;
        System.exit(0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (null != image) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public static void start(Supplier<BufferedImage> source) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            final Viewer comp = new Viewer(source);
            final JFrame frame = new JFrame("Viewer");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    comp.stop();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(comp);
            frame.setBackground(Color.BLACK);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setPreferredSize(new java.awt.Dimension(640, 480));
            frame.setLocation(300, 200);
            frame.pack();
            frame.setVisible(true);
            comp.start();
        });
    }

}
