package de.mknblch.contours;

/**
 * @author mknblch
 */
public interface VideoSource extends AutoCloseable {

    int fps();

    Image image();

}
