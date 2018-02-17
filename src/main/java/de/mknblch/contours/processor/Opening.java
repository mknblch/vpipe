package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Opening implements Processor {

    @Override
    public Image compute(Image image) {
        Erosion.transform(image);
        Dilation.transform(image);
        return image;
    }
}
