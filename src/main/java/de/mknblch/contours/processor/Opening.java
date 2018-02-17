package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Opening extends Processor<Image, Image> {

    @Override
    public Image compute(Image image) {
        Erosion.transform(image);
        Dilation.transform(image);
        return image;
    }
}
