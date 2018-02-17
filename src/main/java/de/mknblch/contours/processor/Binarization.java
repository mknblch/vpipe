package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Binarization implements Processor {

    private final int threshold;
    private Image image = null;

    public Binarization(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Image compute(Image in) {
        if (null == image) {
            image = new Image(in.width, in.height, Image.Type.MONOCHROM);
        }
        for (int i = 0; i < image.data.length; i++) {
            image.data[i] = (in.data[i * in.type.channels + Image.Component.RED.value] & 0xFF
                    + in.data[i * in.type.channels + Image.Component.GREEN.value] & 0xFF
                    +in.data[i * in.type.channels + Image.Component.BLUE.value] & 0xFF) / 3
                    > threshold ?
                    (byte) 0xFF :
                    0;
        }
        return image;
    }
}
