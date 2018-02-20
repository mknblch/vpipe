package de.mknblch.contours.processor;

import de.mknblch.contours.Image;

import java.util.function.IntUnaryOperator;

/**
 * @author mknblch
 */
public class PixelProcessor {

    public interface ColorPixelFunction {
        int apply(int r, int g, int b);
    }

    public static class MonochromePixelProcessor extends ImageProcessor<Image> {

        private final IntUnaryOperator function;

        public MonochromePixelProcessor(IntUnaryOperator function) {
            this.function = function;
        }

        @Override
        protected void computeOut(Image in) {
            Image.requireMonochrom(in);
            adaptTo(in, Image.Type.MONOCHROME);
            final int pixels = in.pixels();
            for (int i = 0; i < pixels; i += 3) {
                out.setValue(i, function.applyAsInt(in.data[i] & 0xFF));
            }
        }
    }

    public static class ColorPixelProcessor extends ImageProcessor<Image> {

        private final ColorPixelFunction function;

        public ColorPixelProcessor(ColorPixelFunction function) {
            this.function = function;
        }

        @Override
        protected void computeOut(Image in) {
            Image.requireColor(in);
            adaptTo(in, Image.Type.COLOR);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        in.data[i + Image.Component.RED.value] & 0xFF,
                        in.data[i + Image.Component.GREEN.value] & 0xFF,
                        in.data[i + Image.Component.BLUE.value] & 0xFF);
                out.setColor(i, Image.Component.RED, v);
                out.setColor(i, Image.Component.GREEN, v);
                out.setColor(i, Image.Component.BLUE, v);
            }
        }
    }

    public static class Grayscale extends ImageProcessor<Image> {

        private final ColorPixelFunction function;

        public Grayscale(ColorPixelFunction function) {
            this.function = function;
        }

        @Override
        protected void computeOut(Image in) {
            Image.requireColor(in);
            adaptTo(in, Image.Type.MONOCHROME);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        in.data[i + Image.Component.RED.value] & 0xFF,
                        in.data[i + Image.Component.GREEN.value] & 0xFF,
                        in.data[i + Image.Component.BLUE.value] & 0xFF);
                out.setValue(i / 3, v);
            }
        }
    }

    public static PixelProcessor.Grayscale grayMean() {
        return new Grayscale((r, g, b) -> (r + g + b) / 3);
    }

}
