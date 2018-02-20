package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

import java.util.Arrays;


/**
 * @author mknblch
 */
public class Dilation extends ImageProcessor<Image> {

    @Override
    protected void computeOut(Image in) {
        Image.requireMonochrom(in);
        adaptTo(in);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int max = 0;
                for (int ty = y - 1; ty <= y + 1; ty++) {
                    for (int tx = x - 1; tx <= x + 1; tx++) {
                        if (ty < 0 || tx < 0 || ty >= height || tx >= width) {
                            continue;
                        }
                        max = Math.max(max, in.getValue(tx, ty));
                    }
                }
                out.setValue(x, y, max);
            }
        }
    }
}
