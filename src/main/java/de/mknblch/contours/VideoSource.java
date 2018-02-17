package de.mknblch.contours;

/**
 * @author mknblch
 */
public interface VideoSource extends AutoCloseable {

    Image image();
}
