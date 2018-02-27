package de.mknblch.vpipe;

import com.github.sarxos.webcam.Webcam;
import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.Source;

import javax.swing.*;
import java.awt.*;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * @author mknblch
 */
public class DefaultVideoSource implements Source<ColorImage>, AutoCloseable {

    private final Webcam webcam;
    private final ColorImage image;

    public DefaultVideoSource() {
        this(chooseCam());
    }

    public DefaultVideoSource(Webcam webcam) {
        this.webcam = webcam;
        webcam.setViewSize(new Dimension(640, 480));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new ColorImage(640, 480);
    }

    @Override
    public void close() {
        webcam.close();
    }

    @Override
    public ColorImage get() {
        final DataBufferByte buffer = (DataBufferByte) webcam
                .getImage()
                .getRaster()
                .getDataBuffer();
        System.arraycopy(buffer.getData(), 0, image.data, 0, image.data.length);
        return image;
    }

    private static Webcam chooseCam() {
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
        return webcam;
    }
}
