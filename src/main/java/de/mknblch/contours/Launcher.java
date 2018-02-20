package de.mknblch.contours;

import de.mknblch.contours.processor.*;

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

//        final Processor<?, Image> source = new DefaultVideoSource()
//                .connectTo(new Convolution(Convolution.SMOOTH_3x3))
//                .connectTo(Split.split(
//                        new ContourProcessor(),
//                        new Split.NoOpProcessor<>()
//                )).connectTo(new Merge.MergeTwo<>(new ContourRenderFunction()));

        final Processor<?, ColorImage> source = new DefaultVideoSource();


        Viewer.start(source);

    }
}
