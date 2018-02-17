package de.mknblch.contours.processor;

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
public class ContourProcessor extends Processor<Image, Image> {

    enum Direction {
        E, S, W, N
    }

    private final Image out;

    private IntPredicate threshold = i -> i > 200;

    private final boolean[] visited = new boolean[640 * 480];


    public ContourProcessor() {

        out = new Image(640, 480, Image.Type.COLOR);
    }

    public ContourProcessor withThresholder(IntPredicate threshold) {
        this.threshold = threshold;
        return this;
    }

    @Override
    public Image compute(Image image) {
        Arrays.fill(visited, false);
        out.fill((byte) 0);

        for (int y = 0; y < image.height; y++) {
            boolean last = false;
            for (int x = 1; x < image.width; x++) {
                final boolean t = threshold.test(image.getValue(x, y) & 0xFF);
                final boolean t1 = threshold.test(image.getValue(x-1, y) & 0xFF);
                if (!visited[y * image.width + x] && !t1 && t) {
                        //System.out.println("found at " + x + " x " + y);
                        chain4(image, last, x, y);
                }
                visited[y * image.width + x] = true;
                last = t;
            }
        }
        return out;
    }

    boolean isPixelLocationLegal (Image i, int x, int y) {
        if (x < 0 || x >= i.width)  return false;
        if (y < 0 || y >= i.height) return false;
        return true;
    }

    private Contour chain4(Image image, boolean last, int x, int y) {

        Direction direction = S;
        int tx = x, ty = y+1;
        do {
            switch (direction) {
                case E:
                    if (tx == image.width || !threshold.test(image.getValue(tx, ty - 1) & 0xFF)) {
                        // n
                        direction = N;
                        visited[ty * image.width + tx] = true;
                        ty--;
                    } else if (ty == image.height || !threshold.test(image.getValue(tx, ty) & 0xFF)) {
                        // e
                        direction = E;
                        tx++;
                    } else {
                        // s
                        direction = S;
                        visited[ty * image.width + tx] = true;
                        ty++;
                    }
                    break;
                case S:
                    if (ty == image.height || !threshold.test(image.getValue(tx, ty) & 0xFF)) {
                        // e
                        direction = E;
                        tx++;
                    } else if (tx == 0 || !threshold.test(image.getValue(tx - 1, ty) & 0xFF)) {
                        // s
                        direction = S;
                        visited[ty * image.width + tx] = true;
                        ty++;
                    } else {
                        // w
                        direction = W;
                        tx--;
                    }
                    break;
                case W:
                    if (tx == 0 || !threshold.test(image.getValue(tx - 1, ty) & 0xFF)) {
                        // s
                        direction = S;
                        visited[ty * image.width + tx] = true;
                        ty++;
                    } else if (ty == 0 || !threshold.test(image.getValue(tx - 1, ty - 1) & 0xFF)) {
                        // w
                        direction = W;
                        tx--;
                    } else {
                        // n
                        direction = N;
                        visited[ty * image.width + tx] = true;
                        ty--;
                    }
                    break;
                case N:
                    if (ty == 0 || !threshold.test(image.getValue(tx - 1, ty - 1) & 0xFF)) {
                        // w
                        direction = W;
                        tx--;
                    } else if (tx == image.width || !threshold.test(image.getValue(tx, ty - 1) & 0xFF)) {
                        // n
                        direction = N;
                        visited[ty * image.width + tx] = true;
                        ty--;
                    } else {
                        // e
                        direction = E;
                        tx++;
                    }
                    break;
            }

            // image.setColor(tx, ty, Image.Component.RED, (byte) 255);
            out.setValue(tx, ty, (byte) 255);

        } while (x != tx || y != ty);

        return null; //contour;
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

        public void add(int v) {
            if (offset > 10000) {
                System.out.println("exit");
                return;
            }
            if (data.length < offset + 1) {
                data = Arrays.copyOf(data, offset + offset / 2);
                //System.out.println(offset);
            }
            data[offset++] = (byte) v;
        }

        public int length() {
            return offset;
        }

        public void forEach(PointConsumer consumer) {
            int tx = x, ty = y;
            for (int i = 0; i < offset; i++) {
                switch (data[i]) {
                    case 0:
                        ty++;
                        break;
                    case 1:
                        tx--;
                        ty++;
                        break;
                    case 2:
                        tx--;
                        break;
                    case 3:
                        tx--;
                        ty--;
                        break;
                    case 4:
                        ty--;
                        break;
                    case 5:
                        tx++;
                        ty--;
                    case 6:
                        tx++;
                    case 7:
                        tx++;
                        ty++;
                    default:

                        break;
                }
                consumer.consume(tx, ty);
            }
        }
    }

}
