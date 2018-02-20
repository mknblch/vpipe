package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Erosion extends ImageProcessor<Image> {

    @Override
    protected void computeOut(Image in) {
        Image.requireMonochrom(in);
        adaptTo(in);
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int min = 255;
                for(int ty = y - 1; ty <= y + 1; ty++){
                    for(int tx = x - 1; tx <= x + 1; tx++){
                        if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                            min = Math.min(min, in.getValue(tx, ty));
                        }
                    }
                }
                out.setValue(x, y, min);
            }
        }
    }
}
