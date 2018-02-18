package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Convolution extends Processor<Image, Image> {

    private final Kernel kernel;
    private Image out;

    public Convolution(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public Image compute(Image image) {

        final int width = image.width();
        final int height = image.height();
        if (null == out) {
            out = new Image(width, height, Image.Type.MONOCHROM);
        }
        final int ow = (kernel.width - 1) / 2;
        final int oh = (kernel.height - 1) / 2;

        y:
        for (int y = 0; y < height; y++) {
            x:
            for (int x = 0; x < width; x++) {
                double t = 0.;
                for (int ty = -oh; ty <= oh; ty++) {
                    for (int tx = -ow; tx <= ow; tx++) {

                        final int ky = y + ty;
                        final int kx = x + tx;
                        if (ky >= height || ky < 0) {
                            continue y;
                        }
                        if (kx >= width || kx < 0) {
                            continue x;
                        }
                        final int v = image.getValue(kx, ky, Image.Component.RED);
                        final double h = kernel.value(tx, ty);

                        // System.out.println(v + " * " + h + " = " + (v * h) + " | t = " + t);

                        t += v * h;
                        //System.out.println(tx + " " + ty);
                    }
                }

//                System.out.println("t = " + t);
                out.setValue(x, y, (int) (t * kernel.multiplier + kernel.addend));
            }
        }
        return out;
    }

    public static class Kernel {

        public final double[] H;
        public final double multiplier;
        public final double addend;
        public final int width;
        public final int height;

        public Kernel(double[] h, int width, int height, double multiplier) {
            this(h, width, height, multiplier, 0);
        }

        public Kernel(double[] h, int width, int height, double multiplier, double addend) {
            H = h;
            this.multiplier = multiplier;
            this.width = width;
            this.height = height;
            this.addend = addend;
        }

        public Kernel(double[] h, double multiplier) {
            this(h, (int) Math.sqrt(h.length), (int) Math.sqrt(h.length), multiplier);
        }

        public Kernel(double[] h, double multiplier, double addend) {
            this(h, (int) Math.sqrt(h.length), (int) Math.sqrt(h.length), multiplier, addend);
        }

        public Kernel(double[] h) {
            this(h, 1., 128);
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
            }, 3
    );

    public static final Kernel STAR_3x3 = new Kernel(
            new double[] {
                    -2, 1, -2,
                    1, 4, 1,
                    -2, 1, -2
            }
    );


    public static final Kernel SMOOTH_5x5 = new Kernel(
            new double[]{
                    1., 1., 1., 1., 1.,
                    1., 1., 1., 1., 1.,
                    1., 1., 1., 1., 1.,
                    1., 1., 1., 1., 1.,
                    1., 1., 1., 1., 1.,
            }, 1. / 25
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
            }, 1, 0
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
            }, 1, 128
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
