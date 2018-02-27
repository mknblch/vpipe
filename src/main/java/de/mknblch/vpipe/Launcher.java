package de.mknblch.vpipe;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Source;
import de.mknblch.vpipe.functions.*;

import java.awt.image.BufferedImage;
import java.util.function.Function;

import static de.mknblch.vpipe.functions.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Source<BufferedImage> pump = new DefaultVideoSource()
                .connectTo(grayscale())
                .connectTo(gamma(30))
                .connectTo(contrast(150))
                .connectTo(renderContour(128, 640, 480))
                .connectTo(toBufferedImage());

        Viewer.start(pump);

    }
}
