package de.mknblch.contours;

/**
 * @author mknblch
 */
public interface Processor extends AutoCloseable {

    Image compute(Image image);
}
