package de.mknblch.vpipe;

import de.mknblch.vpipe.functions.Images;
import de.mknblch.vpipe.functions.Kernels;
import de.mknblch.vpipe.functions.WhiteFilter;
import de.mknblch.vpipe.functions.contours.Contour;
import de.mknblch.vpipe.functions.contours.OverlayRenderer;
import de.mknblch.vpipe.functions.contours.Renderer;
import de.mknblch.vpipe.helper.ImageSource;
import de.mknblch.vpipe.helper.SarxosWebcamSource;
import de.mknblch.vpipe.helper.Viewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.Functions.*;
import static de.mknblch.vpipe.Functions.split;

/**
 * @author mknblch
 */
public class Examples {

    public static void main(String[] args) throws IOException {
        final SarxosWebcamSource source = SarxosWebcamSource.choose();

//        webcam(source);
//        maskImage(source);
//        imageConvolution(source);
//        luminosity(source);
//        testContours(source);
        drawContoursBox(source);
//        drawContoursChildren(source);
//        drawContoursAll(source);
//        contourOverlay(source);
//        splitRGB(source);
//        imageOverlay(source);
    }


    public static void luminosity(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(grayLuminosity())
                .connectTo(contrast(2))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void imageConvolution(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose("PC")
                .connectTo(grayLuminosity())
                .connectTo(split(Function.identity(), convolution(Kernels.SMOOTH_3x3)))
                .connectTo(merge((a, b) -> Images.sub(a, b)))
                .connectTo(binarization(30))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void testContours(Source<Image.Color> source) throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("test.png"))
                        .connectTo(grayscale())
                        .connectTo(contours(128))
                        .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void testImage(Source<Image.Color> source) throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("test.png"))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void webcam(Source<Image.Color> source) throws IOException {

        final Image.Gray mask = ImageSource.load(Examples.class.getClassLoader().getResourceAsStream("test.png"))
                .map(grayscale());

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose("PC")
                .connectTo(whiteFilter(40, WhiteFilter.Mode.MEAN))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }

    public static void maskImage(Source<Image.Color> source) throws IOException {

        final Image.Gray mask = ImageSource.load(Examples.class.getClassLoader().getResourceAsStream("mask.png"))
                .map(grayscale());

        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(mask(mask))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursBox(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(whiteFilter(10))
                .connectTo(contours(30))
                .connectTo(new Renderer.BoundingBox(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursChildren(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(grayscale())
                .connectTo(contours(128))
                .connectTo(new Renderer.Children(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursAll(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursColorize(Source<Image.Color> source) {
        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.Colorize(640, 480, 0, 255, 0))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void contourOverlay(Source<Image.Color> source) {

        final Function<Image.Color, Image.Color> colorColorFunction =
//                grayscale()
//                        .andThen(new Convolution(Kernels.ADAPT))
//                        .andThen(contrast(1.3))
                grayscale(0.0299, 0.587, 0.114)
                        .andThen(contours(90))
                        .andThen(new Renderer.Colorize(640, 480, 255, 50, 0));

        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(split(colorColorFunction, Function.identity()))
                .connectTo(merge((a, b) -> Images.add(a, b)))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }

    public static void imageOverlay(Source<Image.Color> source) throws IOException {

        final HashMap<Integer, OverlayRenderer.Overlay> map = new HashMap<>();
        map.put(19239, new OverlayRenderer.Overlay(
                Examples.class.getClassLoader().getResourceAsStream("19239.jpg")));
        map.put(14399, new OverlayRenderer.Overlay(
                Examples.class.getClassLoader().getResourceAsStream("14399.png")));


        final Function<Image.Color, Image.Gray> preprocessor =
                grayLuminosity();
//                whiteFilter(10, WhiteFilter.Mode.MEAN);

        Viewer.start(source
                .connectTo(split(
                        Function.identity(),
                        preprocessor.andThen(contours(90))))
                .connectTo(new OverlayRenderer(640, 480, map, false)));

//        Viewer.start(source
//                .connectTo(preprocessor)
//                .connectTo(toBufferedImage()));

    }

    public static void splitRGB(Source<Image.Color> source) {

        final int threshold = 128;

        final Function<Image.Color, Image.Color> left = red()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, 255, 0, 0));

        final Function<Image.Color, Image.Color> mid = green()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, 0, 255, 0));

        final Function<Image.Color, Image.Color> right = blue()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, 0, 0, 255));

        final Source<BufferedImage> bufferedImageSource = source
                .connectTo(split(left, mid, right))
                .connectTo(merge((rc, gc, bc) -> Images.add(rc, Images.add(gc, bc))))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }


}
