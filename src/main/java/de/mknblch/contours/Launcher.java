package de.mknblch.contours;

import de.mknblch.contours.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Processor<Image, Image> source = new DefaultVideoSource()
                .connectTo(new Convolution(Convolution.HIGHPASS))
                .connectTo(new Invert())
                .connectTo(new Convolution(Convolution.STAR_3x3));

        Viewer.start(source);
    }
}
