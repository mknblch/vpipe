package de.mknblch.vpipe;

import de.mknblch.vpipe.model.Source;

import java.awt.image.BufferedImage;

import static de.mknblch.vpipe.functions.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Source<BufferedImage> pipe = WebcamSource.choose()
                .connectTo(grayscale())
                .connectTo(gamma(30))
                .connectTo(contrast(150))
                .connectTo(renderContour(128, 640, 480))
                .connectTo(toBufferedImage());

        Viewer.start(pipe);
    }
}
