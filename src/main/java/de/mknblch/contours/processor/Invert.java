package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Invert implements Processor {

    @Override
    public Image compute(Image image) {
        for (int i = 0; i < image.data.length; i++) {
            image.data[i] = (byte) Math.abs(255 - image.data[i]);
        }
        return image;
    }
}
