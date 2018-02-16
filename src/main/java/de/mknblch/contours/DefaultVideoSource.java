package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.DataBufferByte;

/**
 * @author mknblch
 */
public class DefaultVideoSource implements VideoSource {

    private final Webcam webcam;
    private final ImageImpl image;

    public DefaultVideoSource() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640, 480));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new ImageImpl();
    }

    @Override
    public int fps() {
        return 20;
    }

    @Override
    public Image image() {
        final DataBufferByte buffer = (DataBufferByte) webcam
                .getImage()
                .getRaster()
                .getDataBuffer();
        image.data = buffer.getData();
        return image;
    }

    @Override
    public void close() throws Exception {
        webcam.close();
    }

    private final class ImageImpl implements Image {

        private int[] data;

        @Override
        public int[] data() {
            return data;
        }

        @Override
        public int width() {
            return 640;
        }

        @Override
        public int height() {
            return 480;
        }
    }
}
