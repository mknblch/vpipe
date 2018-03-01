package de.mknblch.vpipe;

import de.mknblch.vpipe.functions.Merge;
import de.mknblch.vpipe.functions.PixelProcessor;
import de.mknblch.vpipe.functions.Renderer;
import de.mknblch.vpipe.functions.Split;
import de.mknblch.vpipe.functions.contours.ContourProcessor;
import de.mknblch.vpipe.model.Image;
import de.mknblch.vpipe.functions.contours.Contour;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import static de.mknblch.vpipe.model.Image.B;
import static de.mknblch.vpipe.model.Image.I;

/**
 * @author mknblch
 */
public class Functions {

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


    public static Function<Image.Gray, Collection<Contour>> contours(int threshold) {
        return contours(threshold, 8);
    }

    /**
     * calculate contours of a GrayImage based on a threshold
     * @param threshold a threshold between 0 and 255
     * @param minPerimeter minimum contour length
     */
    public static Function<Image.Gray, Collection<Contour>> contours(int threshold, int minPerimeter) {
        return new ContourProcessor(threshold, minPerimeter);
    }

    /**
     * calculate contours of a GrayImage based on a threshold and render
     * @param threshold a threshold between 0 and 255
     */
    public static Function<Collection<Contour>, Image.Color> renderContour(int threshold, int width, int height) {
        return new Function<Collection<Contour>, Image.Color>() {
            private Image.Color out;

            @Override
            public Image.Color apply(Collection<Contour> in) {
                out = Image.Color.adaptTo(out, width, height);
                out.fill(0);
                in.forEach(c -> c.forEach((x, y) -> out.setColor(x, y, 255, 0, 0)));
                return out;
            }
        };
    }

    /**
     * invert a GrayImage
     */
    public static Function<Image.Gray, Image.Gray> invert() {
        return new Function<Image.Gray, Image.Gray>() {
            private Image.Gray out;
            @Override
            public Image.Gray apply(Image.Gray in) {
                out = Image.Gray.adaptTo(out, in);
                for (int i = 0; i < in.data.length; i++) {
                    out.data[i] = B(Math.abs(255 - I(in, i)));
                }
                return out;
            }
        };
    }

    /**
     * Image.ColorImage binarization based on rgb-mean
     * @param threshold
     */
    public static Function<Image.Color, Image.Gray> binarization(int threshold) {
        return new PixelProcessor.Color2Gray((r, g, b) -> (r + g + b) / 3 >= threshold ? 255 : 0);
    }

    /**
     * Mean RGB
     */
    public static Function<Image.Color, Image.Gray> grayscale() {
        return new PixelProcessor.Color2Gray((r, g, b) -> (r + g + b) / 3);
    }

    /**
     * gamma
     * @param a -255 - 255
     */
    public static Function<Image.Gray, Image.Gray> gamma(int a) {
        return new PixelProcessor.Gray2Gray(b -> b + a);
    }

    /**
     * raise contrast
     * @param f factor
     */
    public static Function<Image.Gray, Image.Gray> contrast(double f) {
        return new PixelProcessor.Gray2Gray(b -> (int)((b - 128) * f) + 128);
    }

    /**
     * closing operation
     */
    public static Function<Image.Gray, Image.Gray> closing() {
        return dilation().andThen(erosion());
    }

    /**
     * opening operation
     */
    public static Function<Image.Gray, Image.Gray> opening() {
        return erosion().andThen(dilation());
    }

    /**
     * pixel dilation
     */
    public static Function<Image.Gray, Image.Gray> dilation() {
        return new Function<Image.Gray, Image.Gray>() {
            private Image.Gray out;
            @Override
            public Image.Gray apply(Image.Gray in) {
                out = Image.Gray.adaptTo(out, in);
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
    public static Function<Image.Gray, Image.Gray> erosion() {
        return new Function<Image.Gray, Image.Gray>() {
            private Image.Gray out;
            @Override
            public Image.Gray apply(Image.Gray in) {
                out = Image.Gray.adaptTo(out, in);
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

    /**
     * transform Image to BufferedImage
     */
    public static <I extends Image> Function<I, BufferedImage> toBufferedImage() {
        return new Renderer<>();
    }
}
