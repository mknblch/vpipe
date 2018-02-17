package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Convolution implements Processor {

    private final Kernel kernel;

    public Convolution(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public Image compute(Image image) {
        final int width = image.width();
        final int height = image.height();
        final int ow = (kernel.width - 1) / 2;
        final int oh = (kernel.height - 1) / 2;
        final byte[] data = image.data();
        final byte[] result = new byte[data.length];

        y:
        for (int y = 0; y < height; y++) {
            x:
            for (int x = 0; x < width; x++) {
                double t = 0;
                for (int ty = -oh; ty < oh; ty++) {
                    for (int tx = -ow; tx < ow; tx++) {
                        final int ky = y + ty;
                        final int kx = x + tx;
                        if (ky > height || ky < 0) {
                            continue y;
                        }
                        if (kx > width || kx < 0) {
                            continue x;
                        }
                        t += (image.getValue(x, y) & 0xFF) * kernel.value(tx, ty);
                    }
                }
                result[y * width + x] = Image.clip(t * kernel.multiplier);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setValue(x, y, result[y * width + x]);
            }
        }
        return image;
    }

    public static class Kernel {

        public final double[] H;
        public final double multiplier;
        public final int width;
        public final int height;

        public Kernel(double[] h, int width, int height, double multiplier) {
            H = h;
            this.multiplier = multiplier;
            this.width = width;
            this.height = height;
        }

        public Kernel(double[] h, double multiplier) {
            this(h, (int) Math.sqrt(h.length), (int) Math.sqrt(h.length), multiplier);
        }

        public Kernel(double[] h) {
            this(h, 1.);
        }

        public double value(int xo, int yo) {
            return H[((height - 1) / 2 + yo) * width + (width - 1) / 2 + xo];
        }
    }

    public static final Kernel SMOOTH_3x3 = new Kernel(
            new double[]{
                    1. / 9, 1. / 9, 1. / 9,
                    1. / 9, 1. / 9, 1. / 9,
                    1. / 9, 1. / 9, 1. / 9
            }, 1.
    );

    public static final Kernel STAR_3x3 = new Kernel(
            new double[] {
                    -1, 1, -1,
                    1, 1, 1,
                    -1, 1, -1
            }
    );


    public static final Kernel SMOOTH_5x5 = new Kernel(
            new double[]{
                    1. / 25, 1. / 25, 1. / 25, 1. / 25, 1. / 25,
                    1. / 25, 1. / 25, 1. / 25, 1. / 25, 1. / 25,
                    1. / 25, 1. / 25, 1. / 25, 1. / 25, 1. / 25,
                    1. / 25, 1. / 25, 1. / 25, 1. / 25, 1. / 25,
                    1. / 25, 1. / 25, 1. / 25, 1. / 25, 1. / 25,
            }
    );


    public static final Kernel SMOOTH_7x7 = new Kernel(
            new double[]{
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
                    1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49, 1. / 49,
            }
    );

    public static final Kernel HIGHPASS = new Kernel(
            new double[]{
                    -1, -1, -1,
                    -1, 8, -1,
                    -1, -1, -1
            }
    );

    public static final Kernel HIGHPASS_5x5 = new Kernel(
            new double[]{
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, 24, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
            }, 10. / 25
    );

    public static final Kernel SOBEL_TD = new Kernel(
            new double[] {
                    -1, -2, -1,
                    0, 0, 0,
                    1, 2, 1
            }, 1. / 4
    );

    public static final Kernel SOBEL_LR = new Kernel(
            new double[] {
                    -1, 0, 1,
                    -2, 0, 2,
                    -1, 0, 1
            }, 1. / 4
    );

    public static final Kernel PREWIT_TD = new Kernel(
            new double[] {
                    -1, -1, -1,
                    0, 0, 0,
                    1, 1, 1
            }, 1. / 4
    );

    public static final Kernel PREWIT_LR = new Kernel(
            new double[] {
                    -1, 0, 1,
                    -1, 0, 1,
                    -1, 0, 1
            }, 1. / 4
    );

    public static final Kernel LAPLACIAN = new Kernel(
            new double[] {
                    0, -1, 0,
                    -1, 4, -1,
                    0, -1, 0
            }
    );

}
