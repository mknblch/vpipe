package de.mknblch.contours;

import de.mknblch.contours.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        /*final Pipeline pipe = Pipeline.builder()
                .add(new Convolution(Convolution.HIGHPASS))
                .add(new Binarization(80))
                .add(new Invert())
                .add(new Convolution(Convolution.SMOOTH_3x3))
                .build();*/

        final Processor<Image, Image> source = new DefaultVideoSource()
                .connectTo(new Convolution(Convolution.HIGHPASS))
                .connectTo(new Invert())
                .connectTo(new Convolution(Convolution.SMOOTH_3x3));


        Viewer.start(source);

    }


}
