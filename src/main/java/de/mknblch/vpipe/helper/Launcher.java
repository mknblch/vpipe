package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Source;
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
//                .connectTo(gamma(20))
                .connectTo(contrast(10))
                .connectTo(contours(128))
                .connectTo(renderBoundingBox(640, 480));
//                .connectTo(renderAll(640, 480));

//        final Source<BufferedImage> pipe = new ImageSource(Paths.get("C:/Users/mk/dev/contours/docs/test.png"))
//                .connectTo(grayscale())
//                .connectTo(contours(128))
//                .connectTo(new Renderer.Children(640, 480));

        Viewer.start(pipe);
    }


}
