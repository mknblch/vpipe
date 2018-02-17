package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.DataBufferByte;

/**
 * @author mknblch
 */
public class DefaultVideoSource extends Processor<Void, Image> implements VideoSource {

    private final Webcam webcam;
    private final Image image;

    public DefaultVideoSource() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640, 480));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new Image(640, 480, Image.Type.COLOR);
    }

    @Override
    public Image image() {
        final DataBufferByte buffer = (DataBufferByte) webcam
                .getImage()
                .getRaster()
                .getDataBuffer();
        System.arraycopy(buffer.getData(), 0, image.data, 0, image.data.length);
        return image;
    }

    @Override
    public void close() throws Exception {
        webcam.close();
    }

    @Override
    public Image pull() {
        return image();
    }

    @Override
    public Image compute(Void image) {
        return null;
    }
}
