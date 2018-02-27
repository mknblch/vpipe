package de.mknblch.vpipe;

import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.PullProcessor;
import de.mknblch.vpipe.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final PullProcessor<?, GrayImage> processor = PullProcessor.from(new DefaultVideoSource())
                .connectTo(Processors.grayscale())
                .connectTo(Processors.contours(128))
                .connectTo(new ContourProcessor.Renderer());

        Viewer.start(processor);

    }
}
