package de.mknblch.contours;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author mknblch
 */
public class Viewer extends JPanel {

    private final Processor<?, Image> source;
    private final BufferedImageRenderer bufferedImageRenderer;

    private volatile boolean running = false;

    public Viewer(Processor<?, Image> source) {
        this.source = source;
        bufferedImageRenderer = new BufferedImageRenderer();
    }

    public void start() {
        System.out.println("start");
        running = true;
        new Thread(() -> {
            while (running) {
                this.repaint();
                try {
                    Thread.sleep((1000 / 20));
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
        final BufferedImage image = bufferedImageRenderer.render(source.pull());
        if (null != image) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public static void start(Processor<?, Image> imageProcessor) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            final Viewer comp = new Viewer(imageProcessor);
            final JFrame frame = new JFrame("Viewer");
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
