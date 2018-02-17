package de.mknblch.contours;

import de.mknblch.contours.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Pipeline pipe = Pipeline.builder()
                .add(new Binarization(30))
                .add(new Invert())
                .build();

        Viewer.start(pipe);

    }


}
