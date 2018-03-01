package de.mknblch.vpipe.model;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author mknblch
 */
public abstract class Image {

    public static int RED = 0;
    public static int GREEN = 1;
    public static int BLUE = 2;

    public final byte[] data;
    public final int width;
    public final int height;

    public Image(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public byte[] data() {
        return data;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int pixels() {
        return width * height;
    }

    public int length() {
        return data.length;
    }

    public void fill(int v) {
        Arrays.fill(data, (byte) v);
    }

    public void setValue(int offset, int value) {
        data[offset] = (byte) value;
    }

    public static int I(byte v) {
        return v & 0xFF;
    }

    public static int I(Image image, int offset) {
        return image.data[offset] & 0xFF;
    }

    public static byte B(int v) {
        return (byte) v;
    }

    public static byte clip(double v) {
        return v > 255 ? (byte) 255 : (v < 0 ? 0 : (byte) v);
    }

    public static int blue(int bgr) {
        return bgr & 0xFF;
    }

    public static int green(int bgr) {
        return (bgr >> 8) & 0xFF;
    }

    public static int red(int bgr) {
        return (bgr >> 16) & 0xFF;
    }

    public static int rgb(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static boolean dimensionEqual(Image a, Image b) {
        requireNonNull(a);
        requireNonNull(b);
        return a.width == b.width &&
                a.height == b.height;
    }

    /**
     * adapt the current image to the type and dimensions of the template
     * @return current or a new image if the size differs
     */
    @SuppressWarnings("unchecked")
    public static <T extends Image> T adaptTo(T current, Image template, Class<T> type) {
        if (null == current ||
                current.width != template.width ||
                current.height != template.height) {
            if (type.isAssignableFrom(Gray.class)) {
                return (T) new Gray(template);
            } else if (type.isAssignableFrom(Color.class)) {
                return (T) new Color(template);
            }
        }
        return current;
    }

    /**
     * color image where each pixel is represented by 3 bytes
     */
    public static class Color extends Image {

        public Color(Image template) {
            this(template.width, template.height);
        }

        public Color(int width, int height) {
            this(new byte[width * height * 3], width, height);
        }

        public Color(byte[] data, int width, int height) {
            super(data, width, height);
        }

        public int getColor(int index, int color) {
            return data[index + color] & 0xFF;
        }

        public int getColor(int x, int y, int color) {
            return data[y * width * 3 + x * 3 + color] & 0xFF;
        }

        public void setColor(int index, int color, int value) {
            data[index * 3 + color] = (byte) value;
        }

        public void setColor(int x, int y, int r, int g, int b) {
            final int o = (y * width + x) * 3;
            data[o] = (byte) r;
            data[o + 1] = (byte) g;
            data[o + 2] = (byte) b;
        }

        public void setColor(int x, int y, int color, int value) {
            data[(y * width + x) * 3 + color] = (byte) value;
        }

        public static Color adaptTo(Color current, Image template) {
            if (null == current || current.width != template.width || current.height != template.height) {
                return new Color(template);
            }
            return current;
        }

        public static Color adaptTo(Color current, int width, int height) {
            if (null == current || current.width != width || current.height != height) {
                return new Color(width, height);
            }
            return current;
        }

    }

    /**
     * gray image where each pixel is represented by one byte
     */
    public static class Gray extends Image {

        public Gray(Image template) {
            this(template.width, template.height);
        }

        public Gray(byte[] data, int width, int height) {
            super(data, width, height);
        }

        public Gray(int width, int height) {
            this(new byte[width * height], width, height);
        }

        public int getValue(int x, int y) {
            return data[y * width + x] & 0xFF;
        }

        public int getValue(int x, int y, int defaultValue) {
            return x < 0 || x >= width || y < 0 || y >= height ? defaultValue : data[y * width + x] & 0xFF;
        }

        public void setValue(int x, int y, int iValue) {
            setValue(x, y, Image.clip(iValue));
        }

        public void setValue(int x, int y, byte value) {
            data[y * width + x] = value;
        }

        public Gray plus(Image image) {
            if (!dimensionEqual(this, image)) {
                throw new IllegalArgumentException(
                        "Wrong dimensions: " + image.width + "x" + image.height + " + " +
                                width + "x" + height
                );
            }
            final Gray out = new Gray(image);
            for (int i = 0; i < data.length; i++) {
                out.data[i] = clip(I(this, i) + I(image, i));
            }
            return out;
        }

        public Gray minus(Image image) {
            if (!dimensionEqual(this, image)) {
                throw new IllegalArgumentException(
                        "Wrong dimensions: " + image.width + "x" + image.height + " - " +
                                width + "x" + height
                );
            }
            final Gray out = new Gray(image);
            for (int i = 0; i < data.length; i++) {
                out.data[i] = clip(I(this, i) - I(image, i));
            }
            return out;
        }

        public Gray mul(Image image) {
            if (!dimensionEqual(this, image)) {
                throw new IllegalArgumentException(
                        "Wrong dimensions: " + image.width + "x" + image.height + " * " +
                                width + "x" + height
                );
            }
            final Gray out = new Gray(image);
            for (int i = 0; i < data.length; i++) {
                out.data[i] = clip(I(this, i) * I(image, i));
            }
            return out;
        }

        public Image mul(double f) {
            final Gray out = new Gray(this);
            for (int i = 0; i < data.length; i++) {
                out.data[i] = clip(I(this, i) * f);
            }
            return out;
        }

        public static Gray adaptTo(Gray current, Image template) {
            if (null == current || current.width != template.width || current.height != template.height) {
                return new Gray(template);
            }
            return current;
        }

        public static Gray adaptTo(Gray current, int width, int height) {
            if (null == current || current.width != width || current.height != height) {
                return new Gray(width, height);
            }
            return current;
        }
    }

}
