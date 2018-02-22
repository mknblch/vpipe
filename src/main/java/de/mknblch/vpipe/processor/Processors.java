package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.ColorImage;
import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.Processor;
import de.mknblch.vpipe.model.Contour;

import java.util.List;

import static de.mknblch.vpipe.model.Image.B;
import static de.mknblch.vpipe.model.Image.I;

/**
 * @author mknblch
 */
public class Processors {

    public static Processor<GrayImage, List<Contour>> contours(int threshold) {
        return new ContourProcessor(threshold);
    }

    public static Processor<GrayImage, GrayImage> invert() {
        return new Processor<GrayImage, GrayImage>() {
            private GrayImage out;
            @Override
            public GrayImage compute(GrayImage in) {
                out = GrayImage.adaptTo(out, in);
                for (int i = 0; i < in.data.length; i++) {
                    out.data[i] = B(Math.abs(255 - I(in, i)));
                }
                return out;
            }
        };
    }

    public static Processor<ColorImage, GrayImage> binarization(int threshold) {
        return new PixelProcessor.ColorToMono((r, g, b) -> (r + g + b) / 3 >= threshold ? 255 : 0);
    }

    public static Processor<ColorImage, GrayImage> grayscale() {
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
                        int min = 255;
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
