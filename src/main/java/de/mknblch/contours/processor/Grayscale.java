package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Grayscale implements Processor {

    @Override
    public Image compute(Image image) {
        final byte[] data = image.data();
        for (int i = 0; i < data.length; i += 3) {
            final int mean = data[i]; //(((data[i] + data[i + 1] + data[i + 2]) / 3) & 0xFF);
            data[i] = (byte) (mean & 0xFF);
            data[i+1] = (byte) (mean & 0xFF);
            data[i+2] = (byte) (mean & 0xFF);
        }
        return image;
    }
}
