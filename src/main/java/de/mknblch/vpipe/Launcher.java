package de.mknblch.vpipe;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.Contour;
import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Source;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.functions.Functions.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Function<ColorImage, Collection<Contour>> left = grayscale()
                .andThen(gamma(20))
                .andThen(contrast(2))
                .andThen(contours(128));

        final Source<BufferedImage> pipe = WebcamSource.choose()
                .connectTo(left)
                .connectTo(Launcher::render)
                .connectTo(toBufferedImage());

        Viewer.start(pipe);
    }

    static ColorImage render(Collection<Contour> contours) {
        ColorImage image = new ColorImage(640, 480);
        contours.forEach(c -> draw(c, image));
        return image;
    }

    static void draw(Contour c, ColorImage image) {
        final boolean outer = c.isOuter();
        c.forEach((x, y) -> {

            if (outer) {
                image.setColor(x, y, 0, 255, 0);
            } else {
                image.setColor(x, y, 255, 0, 0);
            }
        });
        c.forEachChild(c2 -> draw(c2, image));
    }
}
