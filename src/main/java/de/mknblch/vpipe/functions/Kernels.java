package de.mknblch.vpipe.functions;

/**
 * @author mknblch
 */
public class Kernels {

    public static final Convolution.Kernel SMOOTH_3x3 = new Convolution.Kernel(
            new double[]{
                    1, 1, 1,
                    1, 1, 1,
                    1, 1, 1
            }, 1. / 9
    );

    public static final Convolution.Kernel SMOOTH_5x5 = new Convolution.Kernel(
            new double[]{
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1,
            }, 1. / 25
    );

    public static final Convolution.Kernel SMOOTH_7x7 = new Convolution.Kernel(
            new double[]{
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1,
            }, 1. / 49
    );
    public static final Convolution.Kernel STAR_3x3 = new Convolution.Kernel(
            new double[] {
                    -2, 1, -2,
                    1, 4, 1,
                    -2, 1, -2
            }
    );

    public static final Convolution.Kernel HIGHPASS = new Convolution.Kernel(
            new double[]{
                    -1, -1, -1,
                    -1, 8, -1,
                    -1, -1, -1
            }
    );

    public static final Convolution.Kernel HIGHPASS_5x5 = new Convolution.Kernel(
            new double[]{
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, 24, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
            }
    );

    public static final Convolution.Kernel ADAPT = new Convolution.Kernel(
            new double[]{
                    -1, -1, -1,
                    -1, 10, -1,
                    -1, -1, -1,
            }
    );

    public static final Convolution.Kernel ADAPT_5x5 = new Convolution.Kernel(
            new double[]{
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, 26, -1, -1,
                    -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1,
            }
    );

    public static final Convolution.Kernel SOBEL_TD = new Convolution.Kernel(
            new double[] {
                    -1, -2, -1,
                    0, 0, 0,
                    1, 2, 1
            }
    );

    public static final Convolution.Kernel SOBEL_LR = new Convolution.Kernel(
            new double[] {
                    -1, 0, 1,
                    -2, 0, 2,
                    -1, 0, 1
            }
    );

    public static final Convolution.Kernel PREWIT_TD = new Convolution.Kernel(
            new double[] {
                    -1, -1, -1,
                    0, 0, 0,
                    1, 1, 1
            }
    );

    public static final Convolution.Kernel PREWIT_LR = new Convolution.Kernel(
            new double[] {
                    -1, 0, 1,
                    -1, 0, 1,
                    -1, 0, 1
            }
    );

    public static final Convolution.Kernel LAPLACIAN = new Convolution.Kernel(
            new double[] {
                    0, -1, 0,
                    -1, 4, -1,
                    0, -1, 0
            }
    );

}
