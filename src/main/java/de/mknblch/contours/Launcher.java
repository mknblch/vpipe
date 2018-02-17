package de.mknblch.contours;

import de.mknblch.contours.processor.*;

import java.util.List;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {
        final Processor<Image, Image> source = new DefaultVideoSource()
                .connectTo(new Convolution(Convolution.HIGHPASS))
                .connectTo(new Invert())
                .connectTo(new Convolution(Convolution.STAR_3x3))
                .connectTo(new Dilation())
                .connectTo(new ContourProcessor());
        Viewer.start(source);

    }
}
