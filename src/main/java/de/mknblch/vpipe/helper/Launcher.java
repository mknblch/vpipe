package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.Kernels;
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

//        final Source<BufferedImage> pipe = new ImageSource(Paths.get("C:\\Users\\mk\\dev\\contours\\docs\\direction.png"))
        final Source<BufferedImage> pipe = SarxosWebcamSource.choose("logitech")
                .connectTo(grayscale())
                .connectTo(convolution(Kernels.ADAPT))
//                .connectTo(erosion())
//                .connectTo(dilation())
//                .connectTo(gamma(30))
                .connectTo(contrast(2))
                .connectTo(contours(128, (perimeter, area, x0, y0, x1, y1) -> Math.abs(area) > 10))
//                .connectTo(info())
                .connectTo(new Renderer.Children(640, 480));
//                .connectTo(new Renderer.Native(640, 480))
//                .connectTo(toBufferedImage());

        Viewer.start(pipe);
    }


}
