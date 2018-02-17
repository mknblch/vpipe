package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;

/**
 * @author mknblch
 */
public class ContourProcessor extends Processor<Image, List<ContourProcessor.Contour>> {

    public interface Acceptor {
        boolean accept(byte ref, byte v);
    }

    private static final int NROW[] = {
            0, -1, -1, -1, 0, 1, 1, 1
    };

    private static final int NCOL[] = {
            1, 1, 0, -1, -1, -1, 0, 1
    };

    private IntPredicate threshold = i -> i > 30;

    private final boolean[] visited = new boolean[640 * 480];


    private List<Contour> contourList = new ArrayList<>();

    public ContourProcessor() {

    }

    public ContourProcessor withThresholder(IntPredicate threshold) {
        this.threshold = threshold;
        return this;
    }

    @Override
    public List<Contour> compute(Image image) {
        Arrays.fill(visited, false);
        contourList.clear();

        boolean last = false;
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                if (!visited[y * image.width + x]) {
                    final boolean t = threshold.test(image.getValue(x, y));
                    if (last != t) {
                        contourList.add(chain8(image, x, y));
                    }
                    last = t;
                }
            }
        }
        return contourList;
    }

    boolean isPixelLocationLegal (Image i, int x, int y) {
        if (x < 0 || x >= i.width)  return false;
        if (y < 0 || y >= i.height) return false;
        return true;
    }

    private Contour chain8(Image image, int x, int y) {

        final Contour contour = new Contour(x, y);
        boolean val;
        int m, q, r, ii, d, dii;
        int lastdir, jj;

        val = threshold.test(image.data[y * image.width + x]);
        q = x;
        r = y;
        lastdir = 4;

        do {
            m = 0;
            dii = -1;
            for (ii = lastdir + 1; ii < lastdir + 8; ii++) { 	/* Look for next */
                jj = ii % 8;

                int xi = NROW[jj] + q;
                int yi = NCOL[jj] + r;

                if (isPixelLocationLegal(image, xi, yi)) {
                    if (val == threshold.test(image.getValue(xi, yi))) {
                        dii = jj;
                        m = 1;
                        break;
                    }
                }
            }

            if (m != 0) { /* Found the next pixel ... */
                contour.add(lastdir);
                System.out.println();
                visited[r * image.width + q] = true;
                q += NROW[dii];
                r += NCOL[dii];
                lastdir = (dii + 5) % 8;
            } else {
                break;
            }
        }
        while ((q != x) || (r != y));
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

        public void add(int v) {
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
