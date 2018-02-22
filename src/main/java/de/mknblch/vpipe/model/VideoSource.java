package de.mknblch.vpipe.model;

/**
 * @author mknblch
 */
public interface VideoSource extends AutoCloseable {

    Image image();
}
