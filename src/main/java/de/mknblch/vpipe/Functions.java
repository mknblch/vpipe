package de.mknblch.vpipe;

import de.mknblch.vpipe.functions.*;
import de.mknblch.vpipe.functions.contours.ContourProcessor;
import de.mknblch.vpipe.functions.contours.Renderer;
import de.mknblch.vpipe.functions.contours.Contour;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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


    public static Function<Image.Gray, List<Contour>> contours(int threshold) {
        return contours(threshold, 8);
    }

    /**
     * calculate contours of a GrayImage based on a threshold
     * @param threshold a threshold between 0 and 255
     * @param minPerimeter minimum contour length
     */
    public static Function<Image.Gray, List<Contour>> contours(int threshold, int minPerimeter) {
        return new ContourProcessor(threshold, minPerimeter);
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
