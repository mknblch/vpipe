package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.Image;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import static de.mknblch.vpipe.Image.I;

/**
 * @author mknblch
 */
public class PixelProcessor {

    @FunctionalInterface
    public interface ColorIntensityFunction {
        int apply(int r, int g, int b);
    }

    private PixelProcessor() {}

    /**
     * pixel value based gray to gray image transformer
     */
    public static class Gray2Gray implements Function<Image.Gray, Image.Gray> {

        private final IntUnaryOperator function;
        private Image.Gray out = null;

        public Gray2Gray(IntUnaryOperator function) {
            this.function = function;
        }

        @Override
        public Image.Gray apply(Image.Gray in) {
            out = Image.Gray.adaptTo(out, in);
            final int pixels = in.pixels();
            for (int i = 0; i < pixels; i ++) {
                out.setValue(i, Image.Gray.clip(function.applyAsInt(I(in, i))));
            }
            return out;
        }
    }

    /**
     * pixel value based color to colorFilter image transformer
     */
    public static class Color2Color implements Function<Image.Color, Image.Color> {

        private Image.Color out = null;
        private final ColorIntensityFunction function;

        public Color2Color(ColorIntensityFunction function) {
            this.function = function;
        }

        @Override
        public Image.Color apply(Image.Color in) {
            out = Image.Color.adaptTo(out, in);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        I(in, i + Image.Color.RED),
                        I(in, i + Image.Color.GREEN),
                        I(in, i + Image.Color.BLUE));
                out.setValue(i + Image.Color.RED, Image.Color.red(v));
                out.setValue(i + Image.Color.GREEN, Image.Color.green(v));
                out.setValue(i + Image.Color.BLUE, Image.Color.blue(v));
            }
            return out;
        }
    }


    /**
     * pixel value based color to gray image transformer
     */
    public static class Color2Gray implements Function<Image.Color, Image.Gray> {

        private Image.Gray out = null;
        private final ColorIntensityFunction function;

        public Color2Gray(ColorIntensityFunction function) {
            this.function = function;
        }

        @Override
        public Image.Gray apply(Image.Color in) {
            out = Image.Gray.adaptTo(out, in);
            out.fill(0);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        I(in, i),
                        I(in, i + 1),
                        I(in, i + 2));
                out.setValue(i / 3, v);
            }
            return out;
        }
    }


    /**
     * pixel value based gray to color image transformer
     */
    public static class Gray2Color implements Function<Image.Gray, Image.Color> {

        private Image.Color out = null;
        private final ColorIntensityFunction function;

        public Gray2Color(ColorIntensityFunction function) {
            this.function = function;
        }

        @Override
        public Image.Color apply(Image.Gray in) {
            out = Image.Color.adaptTo(out, in);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i ++) {
                final int v = in.getValue(i);
                final int r = function.apply(v, v, v);
                final int i3 = i * 3;
                out.setColor(i3, Image.Color.RED, Image.Color.red(r));
                out.setColor(i3, Image.Color.GREEN, Image.Color.green(r));
                out.setColor(i3, Image.Color.BLUE, Image.Color.blue(r));
            }
            return out;
        }
    }
}
