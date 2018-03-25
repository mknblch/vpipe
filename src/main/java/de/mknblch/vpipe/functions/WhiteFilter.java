package de.mknblch.vpipe.functions;

/**
 * @author mknblch
 */
public class WhiteFilter extends PixelProcessor.Color2Gray {

    public enum Mode {
        SIMPLE, MEAN, MAX;
    }

    public WhiteFilter(Mode mode, int threshold) {
        super(mode == Mode.SIMPLE ? simpleFunction(threshold) : (mode == Mode.MEAN ? meanFunction(threshold) : maxFunction(threshold)));
    }

    public static PixelProcessor.Color2Gray simple(int threshold) {
        return new PixelProcessor.Color2Gray(simpleFunction(threshold));
    }

    public static PixelProcessor.Color2Gray mean(int threshold) {
        return new PixelProcessor.Color2Gray(meanFunction(threshold));
    }

    public static PixelProcessor.Color2Gray max(int threshold) {
        return new PixelProcessor.Color2Gray(maxFunction(threshold));
    }

    public static PixelProcessor.ColorIntensityFunction simpleFunction(int threshold) {
        final int t = threshold * 3;
        return (r, g, b) -> (765 - r - g - b) > t ? 0 : (r + g + b) / 3;
    }

    public static PixelProcessor.ColorIntensityFunction meanFunction(int threshold) {
        return (r, g, b) -> {
            final int m = (r + g + b) / 3;
            return ((Math.abs(m - r) +
                    Math.abs(m - g) +
                    Math.abs(m - b)) / 3) > threshold ? 0 : m;
        };
    }

    public static PixelProcessor.ColorIntensityFunction maxFunction(int threshold) {
        return (r, g, b) -> {
            final int m = (r + g + b) / 3;
            return Math.max(Math.abs(m - r), Math.max(Math.abs(m - g), Math.abs(m - b))) > threshold ? 0 : m;
        };
    }

}
