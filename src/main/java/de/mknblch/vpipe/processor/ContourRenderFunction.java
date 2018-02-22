package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.GrayImage;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author mknblch
 */
public class ContourRenderFunction implements BiFunction<List<Contour>, GrayImage, GrayImage> {

    @Override
    public GrayImage apply(List<Contour> contours, GrayImage image) {

        image.fill(0);
        contours.forEach(c ->
                c.forEach((x, y) -> {
                    image.setValue(x, y, 255);
                })
        );
        return image;
    }
}
