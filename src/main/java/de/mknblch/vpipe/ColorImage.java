package de.mknblch.vpipe;

/**
 * @author mknblch
 */
public class ColorImage extends Image {

    public static int RED = 0;
    public static int GREEN = 1;
    public static int BLUE = 2;

    public ColorImage(Image template) {
        this(template.width, template.height);
    }

    public ColorImage(int width, int height) {
        this(new byte[width * height * 3], width, height);
    }

    public ColorImage(byte[] data, int width, int height) {
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

    public static ColorImage adaptTo(ColorImage current, Image template) {
        if (null == current || current.width != template.width || current.height != template.height) {
            return new ColorImage(template);
        }
        return current;
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
}
