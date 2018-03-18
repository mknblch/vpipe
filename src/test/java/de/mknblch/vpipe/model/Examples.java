package de.mknblch.vpipe.model;

import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.Images;
import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.contours.Renderer;
import de.mknblch.vpipe.helper.ImageSource;
import de.mknblch.vpipe.helper.SarxosWebcamSource;
import de.mknblch.vpipe.helper.Viewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;

import static de.mknblch.vpipe.Functions.*;
import static de.mknblch.vpipe.Functions.split;

/**
 * @author mknblch
 */
public class Examples {

    public static void main(String[] args) throws IOException {

        splitRGB();
    }

    public static void testContours() throws IOException {
        final Source<BufferedImage> bufferedImageSource =
                new ImageSource(Examples.class.getClassLoader().getResourceAsStream("test.png"))
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

    public static void drawContours() {
        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(grayLuminosity())
                .connectTo(contours(128))
                .connectTo(new Renderer.All(640, 480));
        Viewer.start(bufferedImageSource);
    }

    public static void splitProcessing() {

        final Function<Image.Color, Image.Color> colorColorFunction =
                grayscale(0.299, 0.587, 0.114)
                .andThen(contours(128))
                .andThen(new Renderer.Native(640, 480));

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(split(colorColorFunction, Function.identity()))
                .connectTo(merge((a, b) -> Images.add(a, b)))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }

    public static void splitRGB() {

        final int threshold = 128;

        final Function<Image.Color, Image.Color> left = red()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, Image.RED));

        final Function<Image.Color, Image.Color> mid = green()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, Image.GREEN));

        final Function<Image.Color, Image.Color> right = blue()
                .andThen(contours(threshold))
                .andThen(new Renderer.Colorize(640, 480, Image.BLUE));

        final Source<BufferedImage> bufferedImageSource = SarxosWebcamSource.choose()
                .connectTo(split(left, mid, right))
//                .connectTo(merge((rc, gc, bc) -> Images.add(rc, bc)))
                .connectTo(merge((rc, gc, bc) -> Images.add(rc, Images.add(gc, bc))))
                .connectTo(toBufferedImage());

        Viewer.start(bufferedImageSource);
    }


}
