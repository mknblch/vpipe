package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Pipeline pipe = Pipeline.builder()
                .build();

        javax.swing.SwingUtilities.invokeLater(() -> {

            final JFrame frame = new JFrame("bla");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            final Viewer comp = new Viewer(pipe);
            frame.getContentPane().add(comp);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setPreferredSize(new java.awt.Dimension(800, 600));
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
            comp.start();

        });

    }


}
