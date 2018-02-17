package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;
import com.sun.tools.doclint.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * @author mknblch
 */
public class Viewer extends JPanel {

    private final VideoSource source;
    private final BufferedImageRenderer bufferedImageRenderer;

    private volatile boolean running = false;

    public Viewer(VideoSource source) {
        this.source = source;
        bufferedImageRenderer = new BufferedImageRenderer();
    }


    public void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                this.repaint();
                try {
                    Thread.sleep((1000 / source.fps()));
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final BufferedImage image = bufferedImageRenderer.render(source.image());
        if (null != image) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public static void start(Pipeline pipe) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            final Viewer comp = new Viewer(pipe);
            final JFrame frame = new JFrame("Viewer");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        comp.stop();
                        pipe.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    frame.dispose();
                    System.exit(0);
                }
            });
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
