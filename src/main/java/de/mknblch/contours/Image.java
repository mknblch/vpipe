package de.mknblch.contours;

import java.util.Arrays;

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
        COLOR(3), MONOCHROM(1);
        public final int channels;
        Type(int channels) {
            this.channels = channels;
        }
    }

    public final byte[] data;
    public final int width;
    public final int height;
    public final Type type;

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

    public int length() {
        return data.length;
    }

    public void fill(byte v) {
        Arrays.fill(data, v);
    }

    public byte getValue(int x, int y) {
        return data()[y * width() * type.channels + x * type.channels];
    }

    public byte getValue(int x, int y, Component colorComponent) {
        return data()[y * width() * type.channels + x * type.channels + colorComponent.value];
    }

    public void setColor(int x, int y, Component component, byte value) {
        data[y * width() * type.channels + x * type.channels + component.value] = value;
    }

    public void setValue(int x, int y, byte value) {
        if (type == Type.COLOR) {
            data[y * width() * type.channels + x * type.channels] = value;
            data[y * width() * type.channels + x * type.channels + 1] = value;
            data[y * width() * type.channels + x * type.channels + 2] = value;
        } else {
            data[y * width() * type.channels + x * type.channels] = value;
        }
    }

    public void setValue(int offset, byte value) {
        if (type == Type.COLOR) {
            data[offset] = value;
            data[offset + 1] = value;
            data[offset + 2] = value;
        } else {
            data[offset] = value;
        }
    }

    public static byte clip(double v) {
        return v > 255 ? (byte) 255 : (v < 0 ? 0 : (byte) v);
    }
}
