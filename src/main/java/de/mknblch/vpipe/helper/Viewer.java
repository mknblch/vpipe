package de.mknblch.vpipe.helper;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class Viewer extends JPanel {

    private final Supplier<BufferedImage> source;

    private volatile boolean running = false;

    private long fps;

    private ExecutionTimer<BufferedImage, BufferedImage> timer;

    public Viewer(Supplier<BufferedImage> supplier) {
        this.source = supplier;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        timer = new ExecutionTimer<BufferedImage, BufferedImage>(Function.identity(), 20)
                .withListener((function, duration) -> fps = 1000 / duration);
    }

    public void start() {
        running = true;

        new Thread(() -> {
            while (running) {
                this.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final BufferedImage image = timer.apply(source.get());
        if (null != image) {
            g.drawImage(image, 0, 0, this);
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
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // TODO
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
