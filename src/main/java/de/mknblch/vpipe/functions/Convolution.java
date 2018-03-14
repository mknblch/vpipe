package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.Image;

import java.util.function.Function;

/**
 * @author mknblch
 */
public class Convolution implements Function<Image.Gray, Image.Gray> {

    private final Kernel kernel;
    private Image.Gray out;

    public Convolution(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public Image.Gray apply(Image.Gray in) {
        out = Image.Gray.adaptTo(out, in);
        out.fill(0);
        final int ow = (kernel.width - 1) / 2;
        final int oh = (kernel.height - 1) / 2;
        y:
        for (int y = 0; y < in.height; y++) {
            x:
            for (int x = 0; x < in.width; x++) {
                double t = 0.;
                for (int ty = -oh; ty <= oh; ty++) {
                    for (int tx = -ow; tx <= ow; tx++) {
                        final int ky = y + ty;
                        final int kx = x + tx;
                        if (ky >= in.height || ky < 0) {
                            continue y;
                        }
                        if (kx >= in.width || kx < 0) {
                            continue x;
                        }
                        t += in.getValue(kx, ky) * kernel.value(tx, ty);
                    }
                }
                out.setValue(x, y, (int) (t * kernel.multiplier));
            }
        }
        return out;
    }

    public static class Kernel {

        public final double[] H;
        public final double multiplier;
        public final int width;
        public final int height;
        private final int h2;
        private final int w2;

        public Kernel(double[] h, int width, int height, double multiplier) {
            this.H = h;
            this.multiplier = multiplier;
            this.width = width;
            this.height = height;
            this.h2 = (height - 1) / 2;
            this.w2 = (width - 1) / 2;
        }

        public Kernel(double[] h, double multiplier) {
            this(h, (int) Math.sqrt(h.length), (int) Math.sqrt(h.length), multiplier);
        }

        public Kernel(double[] h) {
            this(h, 1.);
        }

        public double value(int xo, int yo) {
            return H[(h2 + yo) * width + w2 + xo];
        }
    }


}
