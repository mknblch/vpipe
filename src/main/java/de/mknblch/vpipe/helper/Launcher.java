package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.contours.ContourProcessor;
import de.mknblch.vpipe.functions.contours.Renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

import static de.mknblch.vpipe.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) throws IOException {

        final Source<BufferedImage> pipe = SarxosWebcamSource.choose()
                .connectTo(grayscale())
                .connectTo(gamma(20))
                .connectTo(contrast(10))
                .connectTo(contours(128))
                .connectTo(new ContourProcessor.Filter(
                        c -> c.perimeter() > 20_000
                ))
                .connectTo(new ContourProcessor.Info())
                .connectTo(renderAll(640, 480));

        Viewer.start(pipe);
    }


}
