package de.mknblch.contours.processor;

import de.mknblch.contours.Image;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author mknblch
 */
public class ContourRenderFunction implements BiFunction<List<ContourProcessor.Contour>, Image, Image> {

    @Override
    public Image apply(List<ContourProcessor.Contour> contours, Image image) {

        image.fill(0);
        contours.forEach(c ->
                c.forEach((x, y) -> {
                    image.setValue(x, y, 255);
                })
        );
        return image;
    }
}
