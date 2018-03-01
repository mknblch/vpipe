package de.mknblch.vpipe;

import de.mknblch.vpipe.functions.contours.Contour;
import de.mknblch.vpipe.model.Source;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Function;

import static de.mknblch.vpipe.Functions.*;
import static de.mknblch.vpipe.model.Image.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Function<Color, Collection<Contour>> left = grayscale()
                .andThen(gamma(20))
                .andThen(contrast(2))
                .andThen(contours(128, 8));

        final Source<BufferedImage> pipe = WebcamSource.choose()
                .connectTo(left)
                .connectTo(Launcher::render)
                .connectTo(toBufferedImage());

        Viewer.start(pipe);
    }

    static Color render(Collection<Contour> contours) {
        Color image = new Color(640, 480);
        contours.forEach(c -> draw(c, image));
        return image;
    }

    static void draw(Contour c, Color image) {
        final int d = c.getDepth();
        int r = d * 20;
        int g = d * 40;
        int b = d * 90;
        c.forEach((x, y) -> {
            image.setColor(x, y, clip(r), clip(g), clip(b));
        });
    }
}
