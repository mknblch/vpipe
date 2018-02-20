package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

public class Dilation extends Processor<Image, Image> {

    @Override
    public Image compute(Image image) {
        return transform(image);
    }

    /**
     * This method will perform dilation operation on the grayscale image img.
     *
     * @param img The image on which dilation operation is performed
     */
    public static Image transform(Image img){


        int width = img.width();
        int height = img.height();

        final Image out = new Image(width, height, img.type);

        //buff
        int buff[];

        //perform dilation
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                buff = new int[9];
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
                out.setValue(x, y, (byte) (buff[8] & 0xFF));
            }
        }

        return out;
    }
}
