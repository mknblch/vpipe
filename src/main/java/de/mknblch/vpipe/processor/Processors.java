package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.Processor;
import de.mknblch.vpipe.model.Contour;

import java.util.List;
import java.util.function.BiFunction;

import static de.mknblch.vpipe.model.Image.B;
import static de.mknblch.vpipe.model.Image.I;

/**
 * @author mknblch
 */
public class Processors {

    /**
     * Split computation into 2 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <I, L, R> Processor<I, Split.TupleTwo<L, R>> split(Processor<I, L> leftProcessor, Processor<I, R> rightProcessor) {
        return new Split.SplitTwo<>(leftProcessor, rightProcessor);
    }

    /**
     * Split computation into 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <I, L, M, R> Processor<I, Split.TupleThree<L, M, R>> split(Processor<I, L> leftProcessor, Processor<I, M> middleProcessor, Processor<I, R> rightProcessor) {
        return new Split.SplitThree<>(leftProcessor, middleProcessor, rightProcessor);
    }

    /**
     * Merge 2 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, R, O> Processor<Split.TupleTwo<L, R>, O> merge(BiFunction<L, R, O> mergeFunction) {
        return new Merge.MergeTwo<>(mergeFunction);
    }

    /**
     * Merge 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, M, R, O> Processor<Split.TupleThree<L, M, R>, O> merge(Merge.TriFunction<L, M, R, O> mergeFunction) {
        return new Merge.MergeThree<>(mergeFunction);
    }

    /**
     * calculate contours of a GrayImage based on a threshold
     * @param threshold a threshold between 0 and 255
     */
    public static Processor<GrayImage, List<Contour>> contours(int threshold) {
        return new ContourProcessor(threshold);
    }

    /**
     * calculate contours of a GrayImage based on a threshold and render
     * @param threshold a threshold between 0 and 255
     */
    public static Processor<GrayImage, GrayImage> renderContour(int threshold, int width, int height) {
        return new ContourProcessor(threshold).connectTo(new Processor<List<Contour>, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(List<Contour> in) {
                out = GrayImage.adaptTo(out, width, height);
                out.fill(0);
                in.forEach(c -> c.forEach((x, y) -> out.setValue(x, y, 255)));
                return out;
            }
        });
    }

    /**
     * invert a GrayImage
     */
    public static Processor<GrayImage, GrayImage> invert() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int i = 0; i < in.data.length; i++) {
                    out.data[i] = B(Math.abs(255 - I(in, i)));
                }
                return out;
            }
        };
    }

    /**
     * ColorImage binarization based on rgb-mean
     * @param threshold
     */
    public static Processor<ColorImage, GrayImage> binarization(int threshold) {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3 >= threshold ? 255 : 0);
    }

    /**
     * Mean RGB
     */
    public static Processor<ColorImage, GrayImage> grayscale() {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3);
    }

    /**
     * gamma
     * @param a -255 - 255
     */
    public static Processor<GrayImage, GrayImage> gamma(int a) {
        return new PixelProcessor.Mono(b -> b + a);
    }

    /**
     * raise contrast
     * @param f factor
     */
    public static Processor<GrayImage, GrayImage> contrast(double f) {
        return new PixelProcessor.Mono(b -> (int)((b - 128) * f) + 128);
    }

    /**
     * closing operation
     */
    public static Processor<GrayImage, GrayImage> closing() {
        return dilation().connectTo(erosion());
    }

    /**
     * opening operation
     */
    public static Processor<GrayImage, GrayImage> opening() {
        return erosion().connectTo(dilation());
    }

    /**
     * pixel dilation
     */
    public static Processor<GrayImage, GrayImage> dilation() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int y = 0; y < in.height; y++) {
                    for (int x = 0; x < in.width; x++) {
                        int max = 0;
                        for (int ty = y - 1; ty <= y + 1; ty++) {
                            for (int tx = x - 1; tx <= x + 1; tx++) {
                                if (ty < 0 || tx < 0 || ty >= in.height || tx >= in.width) {
                                    continue;
                                }
                                max = Math.max(max, in.getValue(tx, ty));
                            }
                        }
                        out.setValue(x, y, max);
                    }
                }
                return out;
            }
        };
    }

    /**
     * pixel erosion
     */
    public static Processor<GrayImage, GrayImage> erosion() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int y = 0; y < in.height; y++) {
                    for (int x = 0; x < in.width; x++) {
                        int min = 255;
                        for (int ty = y - 1; ty <= y + 1; ty++) {
                            for (int tx = x - 1; tx <= x + 1; tx++) {
                                if (ty < 0 || tx < 0 || ty >= in.height || tx >= in.width) {
                                    continue;
                                }
                                min = Math.min(min, in.getValue(tx, ty));
                            }
                        }
                        out.setValue(x, y, min);
                    }
                }
                return out;
            }
        };
    }
}
