package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

public class Dilation extends Processor<Image, Image> {

    @Override
    public Image compute(Image image) {
        transform(image);
        return image;
    }

    /**
     * This method will perform dilation operation on the grayscale image img.
     *
     * @param img The image on which dilation operation is performed
     */
    public static void transform(Image img){

        int width = img.width();
        int height = img.height();

        //buff
        byte buff[];

        //output of dilation
        final byte temp[] = new byte[width * height];

        //perform dilation
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                buff = new byte[9];
                int i = 0;
                for(int ty = y - 1; ty <= y + 1; ty++){
                   for(int tx = x - 1; tx <= x + 1; tx++){
                       if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                           buff[i] = img.getValue(tx, ty);
                           i++;
                       }
                   }
                }
                //sort buff
                java.util.Arrays.sort(buff);
                //save highest value
                temp[x + y * width] = buff[8];
            }
        }

        /**
         * Save the erosion value in image img.
         */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                img.setValue(x, y, (byte) temp[x + y * width]);
            }
        }
    }
}
