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

    private static final int MIN_CONTOUR_LENGTH = 15;

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
        for (int i = 0; i < image.data.length - image.width - 1; i++) {
            if (visited.get(i)) {
                continue;
            }
            final int x = i % image.width;
            if (x == image.width - 1) {
                continue;
            }
            final int y = i / image.width;
            if ((x == 0 || !threshold.test(image.getValue(x - 1, y))) && threshold.test(image.getValue(x, y))) {
                final Contour c = chain4(image, i, x, y);
                if (c.length() < MIN_CONTOUR_LENGTH) {
                    continue;
                }
                contours.add(c);
            }
        }
        return contours;
    }

    private Contour chain4(GrayImage image, int i, int sx, int sy) {

        final Contour contour = new Contour(sx, sy);
        Direction d = S;
        int x = sx, y = sy + 1;
        final byte[] data = image.data;
        final int width = image.width;
        int t = i + width;
        do {
            switch (d) {
                case E:
//                    if (x == image.width - 1 || !threshold.test(image.getValue(x, y - 1))) {
                    if (x == width - 1 || !threshold.test(I(data[t - width]))) {
                        visited.set(t);
                        d = N;
                        y--;
                        t -= width;
//                    } else if (y == image.height - 1 || !threshold.test(image.getValue(x, y))) {
                    } else if (y == image.height - 1 || !threshold.test(I(data[t]))) {
                        d = E;
                        x++;
                        t++;
                    } else {
                        visited.set(t);
                        d = S;
                        y++;
                        t += width;
                    }
                    break;
                case S:
//                    if (y == image.height -1 || !threshold.test(image.getValue(x, y))) {
                    if (y == image.height -1 || !threshold.test(I(data[t]))) {
                        d = E;
                        x++;
                        t++;
//                    } else if (x <= 0 || !threshold.test(image.getValue(x - 1, y))) {
                    } else if (x <= 0 || !threshold.test(I(data[t - 1]))) {
                        visited.set(t);
                        d = S;
                        y++;
                        t += width;
                    } else {
                        d = W;
                        x--;
                        t--;
                    }
                    break;
                case W:
//                    if (x <= 0 || !threshold.test(image.getValue(x - 1, y))) {
                    if (x <= 0 || !threshold.test(I(data[t - 1]))) {
                        visited.set(t);
                        d = S;
                        y++;
                        t += width;
//                    } else if (y <= 0 || !threshold.test(image.getValue(x - 1, y - 1))) {
                    } else if (y <= 0 || !threshold.test(I(data[t - width - 1]))) {
                        d = W;
                        x--;
                        t--;
                    } else {
                        visited.set(t);
                        d = N;
                        y--;
                        t -= width;
                    }
                    break;
                case N:
//                    if (y <= 0 || !threshold.test(image.getValue(x - 1, y - 1))) {
                    if (y <= 0 || !threshold.test(I(data[t - width - 1]))) {
                        d = W;
                        x--;
                        t--;
//                    } else if (x == width - 1 || !threshold.test(image.getValue(x, y - 1))) {
                    } else if (x == width - 1 || !threshold.test(I(data[t - width]))) {
                        visited.set(t);
                        d = N;
                        y--;
                        t -= width;
                    } else {
                        d = E;
                        x++;
                        t++;
                    }
                    break;
            }
            contour.add(d, x, y);
//            System.out.println(x + " " + y);
//        } while (sx != x || sy != y);
        } while (i != t);
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
