package de.mknblch.vpipe.model;

import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.functions.Convolution;
import de.mknblch.vpipe.functions.Images;
import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.Kernels;
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

//        testContours();
//        drawContoursBox();
//        drawContoursChildren();
//        drawContoursAll();
//        contourOverlay();
//        splitRGB();
        imageOverlay();
    }

    public static void testContours() throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("test.png"))
                        .connectTo(grayscale())
                        .connectTo(contours(128))
                        .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void testContours2() throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("sub.png"))
                        .connectTo(grayscale())
                        .connectTo(contours(128))
                        .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void testImage() throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("test.png"))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void webcam() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursBox() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.BoundingBox(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursChildren() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(grayLuminosity())
                .connectTo(contours(90))
                .connectTo(new Renderer.Children(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursAll() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void drawContoursColorize() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.Colorize(640, 480, 0, 255, 0))
                .connectTo(toBufferedImage());
        Viewer.start(bufferedImageSource);
    }

    public static void contourOverlay() {

        final Function<Image.Color, Image.Color> colorColorFunction =
//                grayscale()
//                        .andThen(new Convolution(Kernels.ADAPT))
//                        .andThen(contrast(1.3))
                grayscale(0.0299, 0.587, 0.114)
                        .andThen(contours(128))
                        .andThen(new Renderer.Colorize(640, 480, 0, 255, 100));

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(split(colorColorFunction, Function.identity()))
                .connectTo(merge((a, b) -> Images.add(a, b)))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }

    public static void imageOverlay() throws IOException {

        final HashMap<Integer, OverlayRenderer.Overlay> map = new HashMap<>();
        map.put(19239, new OverlayRenderer.Overlay(
                Examples.class.getClassLoader().getResourceAsStream("19239.jpg")));
        map.put(14399, new OverlayRenderer.Overlay(
                Examples.class.getClassLoader().getResourceAsStream("14399.png")));

        final Function<Image.Color, List<Contour>> contourFun = grayscale()
                .andThen(contrast(1.5))
                .andThen(contours(90));

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(split(Function.identity(), contourFun))
                .connectTo(new OverlayRenderer(640, 480, map));

        Viewer.start(bufferedImageSource);
    }

    public static void splitRGB() {

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

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(split(left, mid, right))
//                .connectTo(merge((rc, gc, bc) -> Images.add(rc, bc)))
                .connectTo(merge((rc, gc, bc) -> Images.add(rc, Images.add(gc, bc))))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }


}
