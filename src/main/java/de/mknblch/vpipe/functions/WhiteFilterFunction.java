package de.mknblch.vpipe.functions;

/**
 * @author mknblch
 */
public class WhiteFilterFunction {

    private WhiteFilterFunction() {}

    public static PixelProcessor.ColorIntensityFunction simple(int threshold) {
        return (r, g, b) -> (765 - r - g - b) / 3 > threshold ? 0 : (r + g + b) / 3;
    }

    public static PixelProcessor.ColorIntensityFunction mean(int threshold) {
        return (r, g, b) -> {
            final int m = (r + g + b) / 3;
            return ((Math.abs(m - r) +
                    Math.abs(m - g) +
                    Math.abs(m - b)) / 3) > threshold ? 0 : m;
        };
    }

    public static PixelProcessor.ColorIntensityFunction max(int threshold) {
        return (r, g, b) -> {
            final int m = (r + g + b) / 3;
            return Math.max(Math.abs(m - r), Math.max(Math.abs(m - g), Math.abs(m - b))) > threshold ? 0 : m;
        };
    }

}
