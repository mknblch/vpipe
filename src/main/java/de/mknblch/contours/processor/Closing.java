package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Closing extends Processor<Image, Image> {

    @Override
    public Image compute(Image image) {
        Dilation.transform(image);
        Erosion.transform(image);
        return image;
    }
}
