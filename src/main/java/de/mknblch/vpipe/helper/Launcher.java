package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.Kernels;
import de.mknblch.vpipe.functions.contours.Renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;

import static de.mknblch.vpipe.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) throws IOException {

        final Source<BufferedImage> pipe = SarxosWebcamSource.choose("logitech")
                .connectTo(grayscale())
                .connectTo(convolution(Kernels.ADAPT))
//                .connectTo(dilation())
                .connectTo(closing())
                .connectTo(contrast(2))
                .connectTo(binarization(128))
//                .connectTo(contours(128, (perimeter, signedArea, x0, y0, x1, y1) -> Math.abs(signedArea) > 10))
//                .connectTo(new Renderer.Children(640, 480));
                .connectTo(toBufferedImage());

        Viewer.start(pipe);

    }


}
