package de.mknblch.vpipe;

import de.mknblch.vpipe.functions.*;
import de.mknblch.vpipe.functions.contours.ContourProcessor;
import de.mknblch.vpipe.functions.contours.Renderer;
import de.mknblch.vpipe.functions.contours.Contour;
import de.mknblch.vpipe.helper.StepTimer;
import de.mknblch.vpipe.helper.Timer;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author mknblch
 */
public class Functions {

    /**
     * Split computation into 2 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <I, L, R> Function<I, TupleTwo<L, R>> split(Function<I, L> leftProcessor, Function<I, R> rightProcessor) {
        return new Split.SplitTwo<>(leftProcessor, rightProcessor);
    }

    /**
     * Split computation into 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <I, L, M, R> Function<I, TupleThree<L, M, R>> split(Function<I, L> leftProcessor, Function<I, M> middleProcessor, Function<I, R> rightProcessor) {
        return new Split.SplitThree<>(leftProcessor, middleProcessor, rightProcessor);
    }

    /**
     * Merge 2 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, R, O> Function<TupleTwo<L, R>, O> merge(BiFunction<L, R, O> mergeFunction) {
        return new Merge.MergeTwo<>(mergeFunction);
    }

    /**
     * Merge 3 pipes
     * @author Jiří Kraml (jkraml@avantgarde-labs.de)
     */
    public static <L, M, R, O> Function<TupleThree<L, M, R>, O> merge(Merge.TriFunction<L, M, R, O> mergeFunction) {
        return new Merge.MergeThree<>(mergeFunction);
    }


    public static Function<Image.Gray, List<Contour>> contours(int threshold) {
        return contours(threshold, (perimeter, area, x0, y0, x1, y1) -> Math.abs(area) > 10 && Math.abs(area) < 20_000);
    }

    /**
     * calculate contours of a GrayImage based on a threshold
     * @param threshold a threshold between 0 and 255
     */
    public static Function<Image.Gray, List<Contour>> contours(int threshold, ContourProcessor.Filter filter) {
        return new ContourProcessor(threshold, filter);
    }

    public static Function<List<Contour>, List<Contour>> removeIf(Predicate<Contour> predicate) {
        return contours -> {
            contours.removeIf(predicate);
            return contours;
        };
    }

    public static Function<List<Contour>, List<Contour>> info(Consumer<List<Contour>> consumer) {
        return contours -> {
            consumer.accept(contours);
            return contours;
        };
    }

    public static Function<List<Contour>, List<Contour>> info() {
        return info(l -> {
            System.out.printf("%d contours with perimeter %d%n", l.size(), l.stream().mapToInt(Contour::perimeter).sum());
        });
    }

    public static <I, O> Function<I, O> timer(Function<I, O> function) {
        return new Timer<>(function, 20);
    }

    /**
     * invert a GrayImage
     */
    public static Function<Image.Gray, Image.Gray> invert() {
        return new PixelProcessor.Gray2Gray(b -> 255 - b);
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
     * red
     */
    public static Function<Image.Color, Image.Gray> red() {
        return new PixelProcessor.Color2Gray((r, g, b) -> r);
    }

    /**
     * red
     */
    public static Function<Image.Color, Image.Gray> green() {
        return new PixelProcessor.Color2Gray((r, g, b) -> g);
    }

    /**
     * red
     */
    public static Function<Image.Color, Image.Gray> blue() {
        return new PixelProcessor.Color2Gray((r, g, b) -> b);
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
        return new Morphological.Dilation();
    }

    /**
     * pixel erosion
     */
    public static Function<Image.Gray, Image.Gray> erosion() {
        return new Morphological.Erosion();
    }

    public static Function<Image.Gray, Image.Gray> convolution(Convolution.Kernel kernel) {
        return new Convolution(kernel);
    }

    /**
     * transform Image to BufferedImage
     */
    public static <I extends Image> Function<I, BufferedImage> toBufferedImage() {
        return new BufferedImageTransformer<>();
    }

    public static Function<List<Contour>, BufferedImage> renderDepth(int width, int height) {
        return new Renderer.Depth(width, height);
    }

    public static Function<List<Contour>, BufferedImage> renderAll(int width, int height) {
        return new Renderer.All(width, height);
    }

    public static Function<List<Contour>, BufferedImage> renderBoundingBox(int width, int height) {
        return new Renderer.BoundingBox(width, height);
    }

    public static Function<List<Contour>, BufferedImage> renderHashes(int width, int height, int... hashes) {
        return new Renderer.Hash(width, height, hashes);
    }

}
