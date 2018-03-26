package de.mknblch.vpipe.functions;


import de.mknblch.vpipe.core.Image;

import java.awt.Color;

/**
 * @author mknblch
 */
public class ColorPassFilterBuilder {

    private static final int LOW = 0;

    private double hueValue = 0.;
    private double hueThreshold = 0.1;
    private double saturationValue = 1.;
    private double saturationThreshold = 0.5;

    public ColorPassFilterBuilder withHue(double hue, double threshold) {
        this.hueValue = hue;
        this.hueThreshold = threshold;
        return this;
    }

    public ColorPassFilterBuilder withHue(Color color, double threshold) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        return withHue(hsb[0], threshold);
    }

    public ColorPassFilterBuilder withSaturation(double saturationValue, double threshold) {
        this.saturationValue = saturationValue;
        this.saturationThreshold = threshold;
        return this;
    }

    public PixelProcessor.Color2Color buildColor() {
        final float[] hsb = new float[3];
        return new PixelProcessor.Color2Color((r, g, b) -> {
            if (test(hsb, r, g, b, hueValue, hueThreshold, saturationValue, saturationThreshold)) return LOW;
            return Image.rgb(r, g, b);
        });
    }

    public PixelProcessor.Color2Gray buildIntensity() {
        final float[] hsb = new float[3];
        return new PixelProcessor.Color2Gray((r, g, b) -> {
            if (test(hsb, r, g, b, hueValue, hueThreshold, saturationValue, saturationThreshold)) return LOW;
            return ((int) (hsb[2] * 255));
        });
    }

    public static PixelProcessor.Color2Color build(Color color, double threshold) {
        return new ColorPassFilterBuilder()
                .withHue(color, threshold)
                .buildColor();
    }

    private static boolean test(float[] hsb,
                                int r,
                                int g,
                                int b,
                                double hueValue,
                                double hueThreshold,
                                double saturationValue,
                                double saturationThreshold) {
        Color.RGBtoHSB(r, g, b, hsb);
        return Math.abs(hsb[0] - hueValue) > hueThreshold ||
                Math.abs(hsb[1] - saturationValue) > saturationThreshold;
    }
}
