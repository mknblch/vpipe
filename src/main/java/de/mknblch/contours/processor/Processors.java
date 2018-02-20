package de.mknblch.contours.processor;

import de.mknblch.contours.GrayImage;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public class Processors {

    public static PixelProcessor.ColorToMono binarization(int threshold) {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3 >= threshold ? 255 : 0);
    }

    public static PixelProcessor.ColorToMono grayscale() {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3);
    }

    public static Processor<GrayImage, GrayImage> dilation() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int y = 0; y < in.height; y++) {
                    for (int x = 0; x < in.width; x++) {
                        int max = 0;
                        for (int ty = y - 1; ty <= y + 1; ty++) {
                            for (int tx = x - 1; tx <= x + 1; tx++) {
                                if (ty < 0 || tx < 0 || ty >= in.height || tx >= in.width) {
                                    continue;
                                }
                                max = Math.max(max, in.getValue(tx, ty));
                            }
                        }
                        out.setValue(x, y, max);
                    }
                }
                return out;
            }
        };
    }

    public static Processor<GrayImage, GrayImage> erosion() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int y = 0; y < in.height; y++) {
                    for (int x = 0; x < in.width; x++) {
                        int min = 0;
                        for (int ty = y - 1; ty <= y + 1; ty++) {
                            for (int tx = x - 1; tx <= x + 1; tx++) {
                                if (ty < 0 || tx < 0 || ty >= in.height || tx >= in.width) {
                                    continue;
                                }
                                min = Math.min(min, in.getValue(tx, ty));
                            }
                        }
                        out.setValue(x, y, min);
                    }
                }
                return out;
            }
        };
    }
}
