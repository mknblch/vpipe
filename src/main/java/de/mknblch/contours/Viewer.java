package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;
import com.sun.tools.doclint.Entity;

import javax.swing.*;
import java.awt.*;
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
}
