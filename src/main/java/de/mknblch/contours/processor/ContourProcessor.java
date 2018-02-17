package de.mknblch.contours.processor;

import com.carrotsearch.hppc.IntArrayList;
import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class ContourProcessor extends Processor<Image, ContourProcessor.Contour>{

    public interface Acceptor {
        boolean accept(byte ref, byte v);
    }

    private static final int DI[] = {
            0, -1, -1, -1, 0, 1, 1, 1
    };

    private static final int DJ[] = {
            1, 1, 0, -1, -1, -1, 0, 1
    };

    private final IntArrayList list;
    private Acceptor acceptor = (ref, v) -> ref == v;

    public ContourProcessor() {
        list = new IntArrayList();
    }

    public ContourProcessor withAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
        return this;
    }

    @Override
    public Contour compute(Image image) {

        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                list.clear();
                chain8(image, x, y);
            }
        }

        return null;
    }

    boolean isPixelLocationLegal (Image image, int x, int y) {
        return !(x < 0 || x >= image.width || y < 0 || y >= image.height);
    }

    void chain8(Image image, int x, int y) {

        byte val;
        int m, q, r, ii, d, dii;
        int lastdir, jj;

        val = image.data[y * image.width + x];
        q = x;
        r = y;
        lastdir = 4;

        do {
            m = 0;
            dii = -1;
            for (ii = lastdir + 1; ii < lastdir + 8; ii++) { 	/* Look for next */
                jj = ii % 8;
                if (isPixelLocationLegal(image, DI[jj] + q, DJ[jj] + r)) {
                    if (acceptor.accept(val, image.data[(DJ[jj] + r) * image.width + (DI[jj] + q)])) {
                        dii = jj;
                        m = 1;
                        break;
                    }
                }
            }

            if (m != 0) { /* Found the next pixel ... */
                list.add(lastdir);
                q += DI[dii];
                r += DJ[dii];
                lastdir = (dii + 5) % 8;
            } else {
                break;
            }
        }
        while ((q != x) || (r != y));

        System.out.println(list);
    }



    public static class Contour {

        public final byte[] data;
        public final int x;
        public final int y;

        public Contour(byte[] data, int x, int y) {
            this.data = data;
            this.x = x;
            this.y = y;
        }
    }

}
