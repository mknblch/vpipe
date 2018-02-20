package de.mknblch.contours.processor;

import de.mknblch.contours.GrayImage;
import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;

import static de.mknblch.contours.processor.ContourProcessor.Direction.*;

/**
 * @author mknblch
 */
public class ContourProcessor extends Processor<GrayImage, List<ContourProcessor.Contour>> {

    enum Direction {
        E, S, W, N;
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
                final boolean t1 = threshold.test(image.getValue(x - 1, y) & 0xFF);
                final boolean t = threshold.test(image.getValue(x, y) & 0xFF);
                if (!visited.get(y * image.width + x) && !t1 && t) {
                    contours.add(chain4(image, x, y));
                }
            }
        }
        return contours;
    }

    private Contour chain4(GrayImage image, int x, int y) {

        final Contour contour = new Contour(x, y);
        Direction d = S;
        int tx = x, ty = y + 1;
        do {
            switch (d) {
                case E:
                    if (tx == image.width - 1 || !threshold.test(image.getValue(tx, ty - 1) & 0xFF)) {
                        // n
                        d = N;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty--;
                    } else if (ty == image.height - 1 || !threshold.test(image.getValue(tx, ty) & 0xFF)) {
                        // e
                        d = E;
                        contour.add(d);
                        tx++;
                    } else {
                        // s
                        d = S;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty++;
                    }
                    break;
                case S:
                    if (ty == image.height -1 || !threshold.test(image.getValue(tx, ty) & 0xFF)) {
                        // e
                        d = E;
                        contour.add(d);
                        tx++;
                    } else if (tx <= 0 || !threshold.test(image.getValue(tx - 1, ty) & 0xFF)) {
                        // s
                        d = S;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty++;
                    } else {
                        // w
                        d = W;
                        contour.add(d);
                        tx--;
                    }
                    break;
                case W:
                    if (tx <= 0 || !threshold.test(image.getValue(tx - 1, ty) & 0xFF)) {
                        // s
                        d = S;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty++;
                    } else if (ty <= 0 || !threshold.test(image.getValue(tx - 1, ty - 1) & 0xFF)) {
                        // w
                        d = W;
                        contour.add(d);
                        tx--;
                    } else {
                        // n
                        d = N;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty--;
                    }
                    break;
                case N:
                    if (ty <= 0 || !threshold.test(image.getValue(tx - 1, ty - 1) & 0xFF)) {
                        // w
                        d = W;
                        contour.add(d);
                        tx--;
                    } else if (tx == image.width - 1 || !threshold.test(image.getValue(tx, ty - 1) & 0xFF)) {
                        // n
                        d = N;
                        contour.add(d);
                        visited.set(ty * image.width + tx);
                        ty--;
                    } else {
                        // e
                        d = E;
                        contour.add(d);
                        tx++;
                    }
                    break;
            }
        } while (x != tx || y != ty);
        return contour;
    }

    public interface PointConsumer {

        void consume(int x, int y);
    }

    public static class Contour {

        public byte[] data;
        public final int x;
        public final int y;

        private int offset = 0;

        public Contour(int x, int y) {
            this.data = new byte[64];
            this.x = x;
            this.y = y;
        }

        public void add(Direction d) {
            switch (d) {
                case E:
                    add(0);
                    break;
                case S:
                    add(1);
                    break;
                case W:
                    add(2);
                    break;
                case N:
                    add(3);
                    break;
            }
        }

        public void add(int v) {
            if (offset > LIMIT) {
                return;
            }
            if (data.length < offset + 1) {
                data = Arrays.copyOf(data, offset + offset / 2);
            }
            data[offset++] = (byte) v;
        }

        public int length() {
            return offset;
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

}
