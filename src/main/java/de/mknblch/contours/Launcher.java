package de.mknblch.contours;

import de.mknblch.contours.processor.*;

/**
 * @author mknblch
 */
public class Launcher {

    public static void main(String[] args) {

        final Pipeline pipe = Pipeline.builder()
                .add(new Invert())
                .add(new Convolution(Convolution.LAPLACIAN))
                .build();

        Viewer.start(pipe);

    }


}
