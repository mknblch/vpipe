package de.mknblch.contours;

import de.mknblch.contours.processor.*;

import java.util.List;

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

        /*
        final Processor<?, Image> source = new DefaultVideoSource()
                .connectTo(new Convolution(Convolution.SMOOTH_3x3))
                .connectTo(Parallelize.parallel(
                        new Convolution(Convolution.HIGHPASS),
                        new Parallelize.NoOpProcessor<>()
                )).connectTo(new Merge.MergeTwo<>(
                        (Image l, Image r) ->
                                l.mul(r.mul(2))
                ));
        */

        final Processor<?, Image> source = new DefaultVideoSource()
                .connectTo(PixelProcessor.grayMean());



        Viewer.start(source);

    }
}
