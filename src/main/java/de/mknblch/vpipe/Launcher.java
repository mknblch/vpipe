package de.mknblch.vpipe;

import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Source;
import de.mknblch.vpipe.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Source<MonoImage> pump = new DefaultVideoSource()
                .connectTo(Processors.grayscale())
                .connectTo(Processors.gamma(30))
                .connectTo(Processors.contrast(150))
                .connectTo(Processors.renderContour(128, 640, 480));

        Viewer.start(pump);

    }
}
