package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Erosion extends Processor<Image, Image> {

    @Override
    public Image compute(Image image) {
        transform(image);
        return image;
    }

    /**
     * This method will perform erosion operation on the grayscale image img.
     *
     * @param img The image on which erosion operation is performed
     */
    public static void transform(Image img){
        /**
         * Dimension of the image img.
         */
        int width = img.width();
        int height = img.height();

        //buff
        byte[] buff;

        //output of erosion
        byte output[] = new byte[width*height];

        //perform erosion
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                buff = new byte[9];
                int i = 0;
                for(int ty = y - 1; ty <= y + 1; ty++){
                    for(int tx = x - 1; tx <= x + 1; tx++){
                        /**
                         * 3x3 mask [kernel or structuring element]
                         * [1, 1, 1
                         *  1, 1, 1
                         *  1, 1, 1]
                         */
                        if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                            //pixel under the mask
                            buff[i] = img.getValue(tx, ty);
                            i++;
                        }
                    }
                }

                //sort buff
                java.util.Arrays.sort(buff);

                //save lowest value
                output[x+y*width] = buff[9-i];
            }
        }

        /**
         * Save the erosion value in image img.
         */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                byte v = output[x + y * width];
                img.setValue(x, y, v);
            }
        }
    }
}
