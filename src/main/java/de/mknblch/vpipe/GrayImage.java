package de.mknblch.vpipe;

/**
 * @author mknblch
 */
public class GrayImage extends Image {

    public GrayImage(Image template) {
        this(template.width, template.height);
    }

    public GrayImage(byte[] data, int width, int height) {
        super(data, width, height);
    }

    public GrayImage(int width, int height) {
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

    public GrayImage plus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " + " +
                            width + "x" + height
            );
        }
        final GrayImage out = new GrayImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) + I(image, i));
        }
        return out;
    }

    public GrayImage minus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " - " +
                            width + "x" + height
            );
        }
        final GrayImage out = new GrayImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) - I(image, i));
        }
        return out;
    }

    public GrayImage mul(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " * " +
                            width + "x" + height
            );
        }
        final GrayImage out = new GrayImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) * I(image, i));
        }
        return out;
    }

    public Image mul(double f) {
        final GrayImage out = new GrayImage(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) * f);
        }
        return out;
    }

    public static GrayImage adaptTo(GrayImage current, Image template) {
        if (null == current || current.width != template.width || current.height != template.height) {
            return new GrayImage(template);
        }
        return current;
    }

    public static GrayImage adaptTo(GrayImage current, int width, int height) {
        if (null == current || current.width != width || current.height != height) {
            return new GrayImage(width, height);
        }
        return current;
    }
}
