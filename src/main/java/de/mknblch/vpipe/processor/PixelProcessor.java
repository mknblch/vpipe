package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.MonoImage;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import static de.mknblch.vpipe.model.Image.I;

/**
 * @author mknblch
 */
public class PixelProcessor {

    public interface ColorPixelFunction {
        int apply(int r, int g, int b);
    }

    public static class Mono implements Function<MonoImage, MonoImage> {

        private final IntUnaryOperator function;
        private MonoImage out = null;

        public Mono(IntUnaryOperator function) {
            this.function = function;
        }

        @Override
        public MonoImage apply(MonoImage in) {
            out = MonoImage.adaptTo(out, in);
            final int pixels = in.pixels();
            for (int i = 0; i < pixels; i ++) {
                out.setValue(i, MonoImage.clip(function.applyAsInt(I(in, i))));
            }
            return out;
        }
    }

    public static class Color implements Function<ColorImage, ColorImage> {

        private ColorImage out = null;
        private final ColorPixelFunction function;

        public Color(ColorPixelFunction function) {
            this.function = function;
        }

        @Override
        public ColorImage apply(ColorImage in) {
            out = ColorImage.adaptTo(out, in);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        I(in, i + ColorImage.RED),
                        I(in, i + ColorImage.GREEN),
                        I(in, i + ColorImage.BLUE));
                out.setColor(i, ColorImage.RED, ColorImage.red(v));
                out.setColor(i, ColorImage.GREEN, ColorImage.green(v));
                out.setColor(i, ColorImage.BLUE, ColorImage.blue(v));
            }
            return out;
        }
    }

    public static class ColorToMono implements Function<ColorImage, MonoImage> {

        private MonoImage out = null;
        private final ColorPixelFunction function;

        public ColorToMono(ColorPixelFunction function) {
            this.function = function;
        }

        @Override
        public MonoImage apply(ColorImage in) {
            out = MonoImage.adaptTo(out, in);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        I(in, i + ColorImage.RED),
                        I(in, i + ColorImage.GREEN),
                        I(in, i + ColorImage.BLUE));
                out.setValue(i / 3, v);
            }
            return out;
        }
    }
}
