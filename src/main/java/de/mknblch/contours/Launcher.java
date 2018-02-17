package de.mknblch.contours;

import de.mknblch.contours.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Pipeline pipe = Pipeline.builder()
                .addProcessor(new Grayscale())
                .addProcessor(new Erosion())
                .addProcessor(new Convolution(Convolution.SMOOTH_3x3))
                .addProcessor(new Convolution(Convolution.HIGHPASS))
                .build();

        Viewer.start(pipe);

    }


}
