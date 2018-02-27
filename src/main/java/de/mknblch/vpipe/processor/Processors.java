package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Contour;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    public static <I, L, R> Function<I, Split.TupleTwo<L, R>> split(Function<I, L> leftProcessor, Function<I, R> rightProcessor) {
        return new Split.SplitTwo<>(leftProcessor, rightProcessor);
    }

    /**
     * Split computation into 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <I, L, M, R> Function<I, Split.TupleThree<L, M, R>> split(Function<I, L> leftProcessor, Function<I, M> middleProcessor, Function<I, R> rightProcessor) {
        return new Split.SplitThree<>(leftProcessor, middleProcessor, rightProcessor);
    }

    /**
     * Merge 2 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, R, O> Function<Split.TupleTwo<L, R>, O> merge(BiFunction<L, R, O> mergeFunction) {
        return new Merge.MergeTwo<>(mergeFunction);
    }

    /**
     * Merge 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, M, R, O> Function<Split.TupleThree<L, M, R>, O> merge(Merge.TriFunction<L, M, R, O> mergeFunction) {
        return new Merge.MergeThree<>(mergeFunction);
    }

    /**
     * calculate contours of a GrayImage based on a threshold
     * @param threshold a threshold between 0 and 255
     */
    public static Function<MonoImage, List<Contour>> contours(int threshold) {
        return new ContourProcessor(threshold);
    }

    /**
     * calculate contours of a GrayImage based on a threshold and render
     * @param threshold a threshold between 0 and 255
     */
    public static Function<MonoImage, MonoImage> renderContour(int threshold, int width, int height) {
        return new ContourProcessor(threshold)
                .andThen(new Function<List<Contour>, MonoImage>() {
                    private MonoImage out;
                    @Override
                    public MonoImage apply(List<Contour> in) {
                        out = MonoImage.adaptTo(out, width, height);
                        out.fill(0);
                        in.forEach(c -> c.forEach((x, y) -> out.setValue(x, y, 255)));
                        return out;
                    }
                });
    }

    /**
     * invert a GrayImage
     */
    public static Function<MonoImage, MonoImage> invert() {
        return new Function<MonoImage, MonoImage>() {
            private MonoImage out;
            @Override
            public MonoImage apply(MonoImage in) {
                out = MonoImage.adaptTo(out, in);
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
    public static Function<ColorImage, MonoImage> binarization(int threshold) {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3 >= threshold ? 255 : 0);
    }

    /**
     * Mean RGB
     */
    public static Function<ColorImage, MonoImage> grayscale() {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3);
    }

    /**
     * gamma
     * @param a -255 - 255
     */
    public static Function<MonoImage, MonoImage> gamma(int a) {
        return new PixelProcessor.Mono(b -> b + a);
    }

    /**
     * raise contrast
     * @param f factor
     */
    public static Function<MonoImage, MonoImage> contrast(double f) {
        return new PixelProcessor.Mono(b -> (int)((b - 128) * f) + 128);
    }

    /**
     * closing operation
     */
    public static Function<MonoImage, MonoImage> closing() {
        return dilation().andThen(erosion());
    }

    /**
     * opening operation
     */
    public static Function<MonoImage, MonoImage> opening() {
        return erosion().andThen(dilation());
    }

    /**
     * pixel dilation
     */
    public static Function<MonoImage, MonoImage> dilation() {
        return new Function<MonoImage, MonoImage>() {
            private MonoImage out;
            @Override
            public MonoImage apply(MonoImage in) {
                out = MonoImage.adaptTo(out, in);
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
    public static Function<MonoImage, MonoImage> erosion() {
        return new Function<MonoImage, MonoImage>() {
            private MonoImage out;
            @Override
            public MonoImage apply(MonoImage in) {
                out = MonoImage.adaptTo(out, in);
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
