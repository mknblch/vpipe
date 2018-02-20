package de.mknblch.contours.processor;

import de.mknblch.contours.Image;
import de.mknblch.contours.Processor;

import java.util.function.IntUnaryOperator;

/**
 * @author mknblch
 */
public abstract class ImageProcessor<I> extends Processor<I, Image> {

    protected Image out;
    protected int width, height;

    protected boolean adaptTo(Image source) {
        return adaptTo(source.width, source.height, source.type);
    }

    protected boolean adaptTo(Image source, Image.Type type) {
        return adaptTo(source.width, source.height, type);
    }

    protected boolean adaptTo(int width, int height, Image.Type type) {
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
        computeOut(in);
        return out;
    }

    protected abstract void computeOut(I in);

}

