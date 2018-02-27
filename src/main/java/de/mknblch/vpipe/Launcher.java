package de.mknblch.vpipe;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.Process;
import de.mknblch.vpipe.model.PullProcessor;
import de.mknblch.vpipe.processor.*;

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

//        final Processor<?, GrayImage> source = new DefaultVideoSource()
//                .connectTo(Processors.grayscale())
//                .connectTo(Processors.contours(128))
//                .connectTo(new ContourProcessor.Renderer());

//        final Processor<?, GrayImage> source = new DefaultVideoSource()
//                .connectTo(Processors.grayscale())
//                .connectTo(Processors.erosion())
//                .connectTo(Processors.dilation());

        final PullProcessor<GrayImage, GrayImage> processor = PullProcessor.from(new DefaultVideoSource())
                .connectTo(Processors.grayscale())
                .connectTo(new Convolution(Kernels.HIGHPASS));

        Viewer.start(processor);

    }
}
