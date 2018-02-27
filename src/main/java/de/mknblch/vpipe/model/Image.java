package de.mknblch.vpipe.model;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author mknblch
 */
public abstract class Image {

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

    public static boolean dimensionEqual(Image a, Image b) {
        requireNonNull(a);
        requireNonNull(b);
        return a.width == b.width &&
                a.height == b.height;
    }

    public static boolean dimensionEqual(Image image, int width, int height) {
        requireNonNull(image);
        return image.width == width &&
                image.height == height;
    }

    public static void requireDimensions(Image image, int width, int height) {
        requireNonNull(image);
        if (!dimensionEqual(image, width, height)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + ", " +
                            width + "x" + height
            );
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Image> T adaptTo(T current, Image template, Class<T> type) {
        if (null == current ||
                current.width != template.width ||
                current.height != template.height) {
            if (type.isAssignableFrom(MonoImage.class)) {
                return (T) new MonoImage(template);
            } else if (type.isAssignableFrom(ColorImage.class)) {
                return (T) new ColorImage(template);
            }
        }
        return current;
    }
}
