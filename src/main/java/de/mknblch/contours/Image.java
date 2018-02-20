package de.mknblch.contours;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author mknblch
 */
public class Image {

    public enum Component {
        RED(0), GREEN(1),BLUE(2);
        public final int value;
        Component(int value) {
            this.value = value;
        }
    }

    public enum Type {
        COLOR(3), MONOCHROME(1);
        public final int channels;
        Type(int channels) {
            this.channels = channels;
        }
    }

    public final byte[] data;
    public final int width;
    public final int height;
    public final Type type;

    public Image(Image template) {
        this(template.width, template.height, template.type);
    }

    public Image(byte[] data, int width, int height, Type type) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Image(int width, int height, Type type) {
        this(new byte[width * height * type.channels], width, height, type);
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

    public Type type() {
        return type;
    }

    public int pixels() {
        return width * height;
    }

    public void fill(byte v) {
        Arrays.fill(data, v);
    }

    public Image plus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " (" + image.type.name() + ") + " +
                            width + "x" + height + " (" + type + ")"
            );
        }
        final Image out = new Image(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip((this.data[i] & 0xFF) + (image.data[i] & 0xFF));
        }
        return out;
    }

    public Image minus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " (" + image.type.name() + ") - " +
                            width + "x" + height + " (" + type + ")"
            );
        }

        final Image out = new Image(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip((this.data[i] & 0xFF) - (image.data[i] & 0xFF));
        }
        return out;
    }

    public Image mul(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " (" + image.type.name() + ") * " +
                            width + "x" + height + " (" + type + ")"
            );
        }
        final Image out = new Image(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip((this.data[i] & 0xFF) * (image.data[i] & 0xFF));
        }
        return out;
    }

    public Image mul(double f) {
        final Image out = new Image(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip((this.data[i] & 0xFF) * f);
        }
        return out;
    }

    public int getValue(int index) {
        return data[index] & 0xFF;
    }

    public int getValue(int x, int y) {
        return data[y * width * type.channels + x * type.channels] & 0xFF;
    }

    public int getColor(int index, Component color) {
        requireColor(this);
        return data[index + color.value] & 0xFF;
    }

    public int getColor(int x, int y, Component color) {
        requireColor(this);
        return data[y * width * type.channels + x * type.channels + color.value] & 0xFF;
    }

    public void setColor(int index, Component color, int value) {
        requireColor(this);
        data[index + color.value] = (byte) value;
    }

    public void setColor(int x, int y, int r, int g, int b) {
        requireColor(this);
        final int o = (y * width + x) * 3;
        data[o] = (byte) r;
        data[o + 1] = (byte) g;
        data[o + 2] = (byte) b;
    }

    public void setColor(int x, int y, Component color, int value) {
        requireColor(this);
        data[(y * width + x) * 3 + color.value] = (byte) value;
    }

    public void setValue(int x, int y, int iValue) {
        setValue(x, y, Image.clip(iValue));
    }

    public void setValue(int x, int y, byte value) {
        data[y * width * type.channels + x * type.channels] = value;
    }

    public void setValue(int offset, int value) {
        data[offset] = (byte) value;
    }

    public static byte clip(double v) {
        return v > 255 ? (byte) 255 : (v < 0 ? 0 : (byte) v);
    }

    public static boolean dimensionEqual(Image a, Image b) {
        requireNonNull(a);
        requireNonNull(b);
        return a.width == b.width &&
                a.height == b.height &&
                a.type == b.type;
    }

    public static boolean dimensionEqual(Image image, int width, int height) {
        requireNonNull(image);
        return image.width == width &&
                image.height == height;
    }

    public static void requireMonochrom(Image image) {
        requireNonNull(image);
        if (image.type != Type.MONOCHROME) {
            throw new IllegalArgumentException("Wrong type. MONOCHROME required");
        }
    }

    public static void requireColor(Image image) {
        requireNonNull(image);
        if (image.type != Type.COLOR) {
            throw new IllegalArgumentException("Wrong type. COLOR required");
        }
    }

    public static void requireDimensions(Image image, int width, int height) {
        requireNonNull(image);
        if (!dimensionEqual(image, width, height)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " (" + image.type.name() + ") - " +
                            width + "x" + height
            );
        }
    }
}
