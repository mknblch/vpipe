package de.mknblch.contours.processor;

import de.mknblch.contours.GrayImage;
import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;

import static de.mknblch.contours.Image.B;
import static de.mknblch.contours.Image.I;
import static de.mknblch.contours.processor.ContourProcessor.Direction.*;

/**
 * @author mknblch
 */
public class ContourProcessor extends Processor<GrayImage, List<ContourProcessor.Contour>> {

    enum Direction {
        E(0), S(1), W(2), N(3);

        public final byte v;

        Direction(int v) {
            this.v = (byte) v;
        }
    }

    private static final int LIMIT = 640 * 480;

    private IntPredicate threshold = i -> i > 128;
    private BitSet visited = new BitSet();


    public ContourProcessor withThresholder(IntPredicate threshold) {
        this.threshold = threshold;
        return this;
    }

    @Override
    public List<Contour> compute(GrayImage image) {
        visited.clear();
        final ArrayList<Contour> contours = new ArrayList<>();
        for (int y = 0; y < image.height - 1; y++) {
            for (int x = 1; x < image.width - 1; x++) {
                final boolean t1 = threshold.test(image.getValue(x - 1, y));
                final boolean t = threshold.test(image.getValue(x, y));
                if (!visited.get(y * image.width + x) && !t1 && t) {
                    contours.add(chain4(image, x, y));
                }
            }
        }
        return contours;
    }

    private Contour chain4(GrayImage image, int sx, int sy) {

        final Contour contour = new Contour(sx, sy);
        Direction d = S;
        int x = sx, y = sy + 1;
        do {
            switch (d) {
                case E:
                    if (x == image.width - 1 || !threshold.test(image.getValue(x, y - 1))) {
                        visited.set(y * image.width + x);
                        d = N;
                        y--;
                    } else if (y == image.height - 1 || !threshold.test(image.getValue(x, y))) {
                        d = E;
                        x++;
                    } else {
                        visited.set(y * image.width + x);
                        d = S;
                        y++;
                    }
                    break;
                case S:
                    if (y == image.height -1 || !threshold.test(image.getValue(x, y))) {
                        d = E;
                        x++;
                    } else if (x <= 0 || !threshold.test(image.getValue(x - 1, y))) {
                        visited.set(y * image.width + x);
                        d = S;
                        y++;
                    } else {
                        d = W;
                        x--;
                    }
                    break;
                case W:
                    if (x <= 0 || !threshold.test(image.getValue(x - 1, y))) {
                        visited.set(y * image.width + x);
                        d = S;
                        y++;
                    } else if (y <= 0 || !threshold.test(image.getValue(x - 1, y - 1))) {
                        d = W;
                        x--;
                    } else {
                        visited.set(y * image.width + x);
                        d = N;
                        y--;
                    }
                    break;
                case N:
                    if (y <= 0 || !threshold.test(image.getValue(x - 1, y - 1))) {
                        d = W;
                        x--;
                    } else if (x == image.width - 1 || !threshold.test(image.getValue(x, y - 1))) {
                        visited.set(y * image.width + x);
                        d = N;
                        y--;
                    } else {
                        d = E;
                        x++;
                    }
                    break;
            }
            contour.add(d, x, y);
        } while (sx != x || sy != y);
        return contour;
    }

    public interface PointConsumer {

        void consume(int x, int y);
    }

    public static class Contour {

        public byte[] data;
        public final int x;
        public final int y;

        private int minX = Integer.MAX_VALUE;
        private int maxX = 0;
        private int minY = Integer.MAX_VALUE;
        private int maxY = 0;

        private int offset = 0;

        public Contour(int x, int y) {
            this.data = new byte[16];
            this.x = x;
            this.y = y;
        }

        public void add(Direction d, int x, int y) {
            minX = x < minX ? x : minX;
            maxX = x > maxX ? x : maxX;
            minY = y < minY ? y : minY;
            maxY = y > maxY ? y : maxY;
            if (offset > LIMIT) {
                return;
            }
            if (data.length < offset + 1) {
                data = Arrays.copyOf(data, offset + offset / 3);
            }
            data[offset++] = d.v;
        }

        public int length() {
            return offset;
        }

        public int getMinX() {
            return minX;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        public void forEach(PointConsumer consumer) {
            int tx = x, ty = y + 1;
            for (int i = 0; i < offset; i++) {
                switch (data[i]) {
                    case 0:
                        tx++;
                        break;
                    case 1:
                        ty++;
                        break;
                    case 2:
                        tx--;
                        break;
                    case 3:
                        ty--;
                        break;

                    default:

                        break;
                }
                consumer.consume(tx, ty);
            }
        }
    }

    public static class Renderer extends Processor<List<Contour>, GrayImage> {

        private GrayImage out;

        @Override
        public GrayImage compute(List<Contour> in) {
            out = GrayImage.adaptTo(out, 640, 480);
            out.fill(0);
            in.forEach(c -> c.forEach((x, y) -> out.setValue(x, y, 255)));
            return out;
        }
    }
}
