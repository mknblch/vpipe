package de.mknblch.vpipe;

/**
 * @author mknblch
 */
public interface VideoSource extends AutoCloseable {

    Image image();
}
