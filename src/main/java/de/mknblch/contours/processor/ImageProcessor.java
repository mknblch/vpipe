package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

/**
 * @author mknblch
 */
public abstract class ImageProcessor<I> extends Processor<I, Image> {

    private Image out;
    protected int width, height;

    protected boolean adapt(Image source) {
        return adapt(source.width, source.height, source.type);
    }

    protected boolean adapt(int width, int height, Image.Type type) {
        if (out == null || this.width != width || this.height != height || out.type != type) {
            this.width = width;
            this.height = height;
            out = new Image(width, height, type);
            return true;
        }
        return false;
    }

    public Image getOut() {
        return out;
    }

    @Override
    public Image compute(I in) {
        compute(in, out);
        return out;
    }

    protected abstract void compute(I in, Image out);
}
