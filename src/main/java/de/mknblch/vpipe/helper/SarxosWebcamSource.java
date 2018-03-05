package de.mknblch.vpipe.helper;

import com.github.sarxos.webcam.Webcam;
import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.Source;

import javax.swing.*;
import java.awt.*;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * @author mknblch
 */
public class SarxosWebcamSource implements Source<Image.Color>, AutoCloseable {

    private final Webcam webcam;
    private final Image.Color image;

    public SarxosWebcamSource(Webcam webcam, int width, int height) {
        this.webcam = webcam;
        webcam.setViewSize(new Dimension(width, height));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new Image.Color(width, height);
    }

    @Override
    public void close() {
        webcam.close();
    }

    @Override
    public Image.Color get() {
        webcam.getImageBytes().get(image.data);
        return image;
    }

    public static SarxosWebcamSource choose() {
        Webcam webcam;
        final List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.size() == 1) {
            webcam = webcams.get(0);
        } else {
            final Object[] array = webcams.toArray();
            webcam = (Webcam) JOptionPane.showInputDialog(null,
                    "Choose cam",
                    "Cam:",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    array,
                    array[0]);
            if (null == webcam) {
                System.exit(0);
            }
        }
        return new SarxosWebcamSource(webcam, 640, 480);
    }

}
