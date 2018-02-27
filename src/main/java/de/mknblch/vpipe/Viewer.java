package de.mknblch.vpipe;

import de.mknblch.vpipe.model.Image;
import de.mknblch.vpipe.model.Processor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class Viewer extends JPanel {

    private final Supplier<? extends Image> source;
    private final BufferedImageRenderer bufferedImageRenderer;

    private volatile boolean running = false;

    private int fps;
    private long n, l = System.currentTimeMillis();

    public Viewer(Supplier<? extends Image> supplier) {
        this.source = supplier;
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
        final BufferedImage image = bufferedImageRenderer.render(source.get());
        if (null != image) {
            g.drawImage(image, 0, 0, this);
            n = System.currentTimeMillis();
            fps = (int) (1000 / (n - l + 1));
            l = n;
            g.setColor(Color.GREEN);
            g.drawString("FPS: " + fps, 10, 20);
        }
    }

    public static void start(Supplier<? extends Image> imageProcessor) {

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
