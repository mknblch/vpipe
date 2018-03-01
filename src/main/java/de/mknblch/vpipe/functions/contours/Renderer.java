package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.util.Collection;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.clip;

/**
 * @author mknblch
 */
public class Renderer {

    public static class Depth implements Function<Collection<Contour>, Image.Color> {

        private final Image.Color image;

        public Depth(int width, int height) {
            image = new Image.Color(width, height);
        }

        @Override
        public Image.Color apply(Collection<Contour> contours) {
            image.fill(0);
            contours.forEach(c -> {
                final int d = c.getDepth();
                int r = d * 20;
                int g = d * 40;
                int b = d * 90;
                c.forEach((x, y) -> {
                    image.setColor(x, y, clip(r), clip(g), clip(b));
                });
            });
            return image;
        }
    }

    public static class All implements Function<Collection<Contour>, Image.Color> {

        private final Image.Color image;

        public All(int width, int height) {
            image = new Image.Color(width, height);
        }

        @Override
        public Image.Color apply(Collection<Contour> contours) {
            image.fill(0);
            contours.forEach(c -> {
                c.forEach((x, y) -> {
                    if (c.isLeaf()) {
                        image.setColor(x, y, 255, 0, 0);
                    } else if (c.isWhite()) {
                        image.setColor(x, y, 255, 255, 255);
                    } else {
                        image.setColor(x, y, 0, 255, 255);
                    }
                });
            });
            return image;
        }
    }

    public static class BoundingBox implements Function<Collection<Contour>, Image.Color> {

        private final Image.Color image;

        public BoundingBox(int width, int height) {
            image = new Image.Color(width, height);
        }

        @Override
        public Image.Color apply(Collection<Contour> contours) {
            image.fill(0);
            contours.forEach(c -> {
                draw(c, image);
            });
            return image;
        }

        private static void draw(Contour c, Image.Color image) {
            final int d = c.getDepth();
            int r = d * 20;
            int g = d * 40;
            int b = d * 90;
            for (int x = c.minX; x < c.maxX; x++) {
                for (int y = c.minY; y < c.maxY; y++) {
                    image.setColor(x, y, r, g, b);
                }
            }
        }
    }
}
