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
                .connectTo(contrast(3))
//                .connectTo(erosion())
//                .connectTo(dilation())
                .connectTo(contours(128))
//                .connectTo(info())
                .connectTo(new Renderer.Children(640, 480));
//                .connectTo(new Renderer.Native(640, 480))

        Viewer.start(pipe);
    }


}
