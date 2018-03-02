package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.contours.Renderer;

import java.awt.image.BufferedImage;

import static de.mknblch.vpipe.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Source<BufferedImage> pipe = SarxosWebcamSource.choose()
                .connectTo(grayscale())
                .connectTo(gamma(20))
                .connectTo(contrast(2))
                .connectTo(contours(128))
                .connectTo(new Renderer.Hash(640, 480))
                .connectTo(toBufferedImage());

        Viewer.start(pipe);
    }


}
