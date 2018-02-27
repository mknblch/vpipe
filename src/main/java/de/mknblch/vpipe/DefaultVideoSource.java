package de.mknblch.vpipe;

import com.github.sarxos.webcam.Webcam;
import de.mknblch.vpipe.model.ColorImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class DefaultVideoSource implements Supplier<ColorImage>, AutoCloseable {

    private final Webcam webcam;
    private final ColorImage image;

    public DefaultVideoSource() {

        final List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.size() == 1) {
            webcam = webcams.get(0);
        } else {
            final Object[] array = webcams.toArray();
            webcam = (Webcam) JOptionPane.showInputDialog(null,
                    "Choose cam",
                    "The Choice of a Lifetime",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    array, // Array of choices
                    array[0]);
//            webcam = webcams.get(2);
            if (null == webcam) {
                System.exit(0);
            }
        }

        webcam.setViewSize(new Dimension(640, 480));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new ColorImage(640, 480);
    }

    @Override
    public void close() throws Exception {
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
}
