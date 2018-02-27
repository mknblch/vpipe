package de.mknblch.vpipe;


import de.mknblch.vpipe.model.Image;
import de.mknblch.vpipe.functions.Renderer;
import de.mknblch.vpipe.model.Source;

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

    private volatile boolean running = false;

    private int fps;
    private long n, l = System.currentTimeMillis();

    public Viewer(Supplier<BufferedImage> supplier) {
        this.source = supplier;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void start() {
        running = true;

        new Thread(() -> {
            while (running) {
                this.repaint();
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final BufferedImage image = source.get();
        if (null != image) {
            g.drawImage(image, 0, 0, this);
            n = System.currentTimeMillis();
            fps = (int) (1000 / (n - l + 1));
            l = n;
            g.setColor(Color.GREEN);
            g.drawString("FPS: " + fps, 10, 20);
        }
    }

    public static void start(Supplier<BufferedImage> source) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            final Viewer comp = new Viewer(source);
            final JFrame frame = new JFrame("Viewer");
            frame.addWindowStateListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    comp.stop();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().add(comp);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setPreferredSize(new java.awt.Dimension(640, 480));
            frame.setLocation(300, 200);
            frame.pack();
            frame.setVisible(true);
            comp.start();
        });

    }

}
