package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.Image;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import static de.mknblch.vpipe.Image.I;

/**
 * @author mknblch
 */
public class PixelProcessor {

    public interface ColorPixelFunction {
        int apply(int r, int g, int b);
    }

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

    public static class Color2Color implements Function<Image.Color, Image.Color> {

        private Image.Color out = null;
        private final ColorPixelFunction function;

        public Color2Color(ColorPixelFunction function) {
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
                out.setColor(i, Image.Color.RED, Image.Color.red(v));
                out.setColor(i, Image.Color.GREEN, Image.Color.green(v));
                out.setColor(i, Image.Color.BLUE, Image.Color.blue(v));
            }
            return out;
        }
    }

    public static class Color2Gray implements Function<Image.Color, Image.Gray> {

        private Image.Gray out = null;
        private final ColorPixelFunction function;

        public Color2Gray(ColorPixelFunction function) {
            this.function = function;
        }

        @Override
        public Image.Gray apply(Image.Color in) {
            out = Image.Gray.adaptTo(out, in);
            final int pixels = in.data.length;
            for (int i = 0; i < pixels; i += 3) {
                final int v = function.apply(
                        I(in, i + Image.Color.RED),
                        I(in, i + Image.Color.GREEN),
                        I(in, i + Image.Color.BLUE));
                out.setValue(i / 3, v);
            }
            return out;
        }
    }
}
