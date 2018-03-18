package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.GREEN;
import static de.mknblch.vpipe.Image.RED;
import static de.mknblch.vpipe.Image.clip;

/**
 * @author mknblch
 */
public abstract class Renderer<I> implements Function<I, BufferedImage> {

    protected final BufferedImage image;
    protected final Graphics2D graphics;
    protected final int width;
    protected final int height;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = image.createGraphics();
        graphics.setBackground(Color.BLACK);
    }

    protected void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    protected void setColor(int r, int g, int b) {
        graphics.setColor(new Color(clip(r) & 0xFF, clip(g) & 0xFF, clip(b) & 0xFF));
    }

    @Override
    public BufferedImage apply(I i) {
        render(i, graphics);
        return image;
    }

    abstract void render(I in, Graphics2D graphics);

    public static class Depth extends Renderer<List<Contour>> {

        public Depth(int width, int height) {
            super(width, height);
        }

        @Override
        void render(List<Contour> contours, Graphics2D graphics) {
            clear();
            contours.forEach(c -> {
                final int d = c.getLevel();
                setColor(d * 20, d * 40, d * 90);
                c.forEach((x, y) -> {
                    graphics.drawRect(x, y, 1, 1);
                });
            });
        }
    }

    public static class All extends Renderer<List<Contour>> {

        public All(int width, int height) {
            super(width, height);
        }

        @Override
        void render(List<Contour> in, Graphics2D graphics) {
            clear();
            in.forEach(c -> {
                if (c.isLeaf()) {
                    setColor(255, 0, 0);
                } else if (c.isWhite()) {
                    setColor(0, 255, 0);
                } else {
                    setColor(255, 255, 0);
                }
                c.forEach((x, y) -> {
                    graphics.drawLine(x, y, x, y);
                });
                if (c.getMaxLevel() < 2) {
                    return;
                }
                final double angle = c.getAngle();
                int tx = (int) (Math.cos(angle) * 50 + c.cx());
                int ty = (int) (Math.sin(angle) * 50 + c.cy());
                graphics.drawLine(c.cx(), c.cy(), tx, ty);
            });
        }
    }

    public static class BoundingBox extends Renderer<List<Contour>> {

        public BoundingBox(int width, int height) {
            super(width, height);
        }

        @Override
        void render(List<Contour> in, Graphics2D graphics) {
            clear();
            in.forEach(c -> {
                final int d = c.getLevel();
                setColor(d * 20, d * 40, d * 90);
                graphics.drawRect(c.minX, c.minY, c.width(), c.height());
            });
        }
    }

    public static class Hash extends Renderer<List<Contour>> {

        private final int[] hashes;

        public Hash(int width, int height, int... hashes) {
            super(width, height);
            this.hashes = hashes;
            Arrays.sort(hashes);
        }

        @Override
        void render(List<Contour> in, Graphics2D graphics) {
            clear();
            in.forEach(c -> {
                if (c.getDepth() < 2) {
                    return;
                }
                final int d = c.getDepth();
                if (Arrays.binarySearch(hashes, c.hash()) >= 0) {
                    setColor(d * 80, d * 40, d * 20);
                    c.forEach((x, y) -> {
                        graphics.drawLine(x, y, x, y);
                    });

                    final double angle = c.getAngle();
                    int tx = (int) (Math.cos(angle) * 50 + c.cx());
                    int ty = (int) (Math.sin(angle) * 50 + c.cy());
                    graphics.drawLine(c.cx(), c.cy(), tx, ty);

                }
            });
        }
    }

    public static class Children extends Renderer<List<Contour>> {

        public Children(int width, int height) {
            super(width, height);
        }

        @Override
        void render(List<Contour> in, Graphics2D graphics) {
            clear();
            setColor(255, 255, 255);
            in.forEach(c -> {

                c.forEachChild(child -> {
                    graphics.drawLine(c.cx(), c.cy(), child.cx(), child.cy());
                });

                if (c.getDepth() == 2) {
                    graphics.drawString(String.valueOf(c.hash()), c.x, c.y - 10);
                }
                if (!c.isLeaf()) {
                    return;
                }
                c.forEach((x, y) -> {
                    graphics.drawLine(x, y, x, y);
                });
            });
        }
    }

    public static class Native implements Function<List<Contour>, Image.Color> {

        private Image.Color image;

        private final int width;
        private final int height;

        public Native(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public Image.Color apply(List<Contour> contours) {
            image = Image.Color.adaptTo(image, width, height);
            image.fill(0);
            contours.forEach(c -> {
                c.forEach((x, y) -> {
                    if (c.isLeaf()) {
                        image.setColor(x, y, 255, 0, 0);
                    } else {
                        image.setColor(x, y, 0, 200, 255);
                    }
                });
            });
            return image;
        }
    }
    public static class Colorize implements Function<List<Contour>, Image.Color> {

        private Image.Color image;

        private final int width;
        private final int height;

        private final Contour.PointConsumer consumer;

        public Colorize(int width, int height, int color) {
            this.width = width;
            this.height = height;

            switch (color) {
                case RED:
                    consumer = (x, y) -> image.setColor(x, y, 255, 0, 0);
                    break;
                case GREEN:
                    consumer = (x, y) -> image.setColor(x, y, 0, 255, 0);
                    break;
                default:
                    consumer = (x, y) -> image.setColor(x, y, 0, 0, 255);
                    break;
            }
        }

        @Override
        public Image.Color apply(List<Contour> contours) {
            image = Image.Color.adaptTo(image, width, height);
            image.fill(0);
            contours.forEach(c -> c.forEach(consumer));
            return image;
        }
    }
}
