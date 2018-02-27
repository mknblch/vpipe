package de.mknblch.vpipe.model;

/**
 * @author mknblch
 */
public class MonoImage extends Image {

    public MonoImage(Image template) {
        this(template.width, template.height);
    }

    public MonoImage(byte[] data, int width, int height) {
        super(data, width, height);
    }

    public MonoImage(int width, int height) {
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

    public MonoImage plus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " + " +
                            width + "x" + height
            );
        }
        final MonoImage out = new MonoImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) + I(image, i));
        }
        return out;
    }

    public MonoImage minus(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " - " +
                            width + "x" + height
            );
        }
        final MonoImage out = new MonoImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) - I(image, i));
        }
        return out;
    }

    public MonoImage mul(Image image) {
        if (!dimensionEqual(this, image)) {
            throw new IllegalArgumentException(
                    "Wrong dimensions: " + image.width + "x" + image.height + " * " +
                            width + "x" + height
            );
        }
        final MonoImage out = new MonoImage(image);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) * I(image, i));
        }
        return out;
    }

    public Image mul(double f) {
        final MonoImage out = new MonoImage(this);
        for (int i = 0; i < data.length; i++) {
            out.data[i] = clip(I(this, i) * f);
        }
        return out;
    }

    public static MonoImage adaptTo(MonoImage current, Image template) {
        if (null == current || current.width != template.width || current.height != template.height) {
            return new MonoImage(template);
        }
        return current;
    }

    public static MonoImage adaptTo(MonoImage current, int width, int height) {
        if (null == current || current.width != width || current.height != height) {
            return new MonoImage(width, height);
        }
        return current;
    }
}
