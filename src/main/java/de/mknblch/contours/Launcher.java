package de.mknblch.contours;

import de.mknblch.contours.processor.*;

import static de.mknblch.contours.processor.Kernels.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {
        /*
        final Processor<Image, List<ContourProcessor.Contour>> source = new DefaultVideoSource()
                .connectTo(new Convolution(Convolution.HIGHPASS))
                .connectTo(new Invert())
                .connectTo(new Convolution(Convolution.STAR_3x3))
                .connectTo(new ContourProcessor());
        */

        final Processor<?, GrayImage> source = new DefaultVideoSource()
                .connectTo(Processors.grayscale())
                .connectTo(Split.split(
                        new ContourProcessor(),
                        new Split.NoOpProcessor<>()
                )).connectTo(new Merge.MergeTwo<>(new ContourRenderFunction()));

//        final Processor<?, GrayImage> source = new DefaultVideoSource()
//                .connectTo(Processors.grayscale())
//                .connectTo(Processors.erosion())
//                .connectTo(Processors.dilation());


        Viewer.start(source);

    }
}
