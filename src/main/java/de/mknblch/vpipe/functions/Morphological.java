package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.core.Image;

import java.util.function.Function;

/**
 * @author mknblch
 */
public class Morphological {

    public static class Dilation implements Function<Image.Gray, Image.Gray> {

        private Image.Gray out;

        @Override
        public Image.Gray apply(Image.Gray in) {
            out = Image.Gray.adaptTo(out, in);
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
    }

    public static class Erosion implements Function<Image.Gray, Image.Gray> {

        private Image.Gray out;

        @Override
        public Image.Gray apply(Image.Gray in) {
            out = Image.Gray.adaptTo(out, in);
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
