package de.mknblch.vpipe;

import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.Image;
import de.mknblch.vpipe.model.Process;

import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class TestSource implements Supplier<GrayImage> {

    private final GrayImage image = new GrayImage(new byte[]{
            0, 0, 0, 0, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, (byte) 255, 0, (byte) 255, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, 0, 0, 0, 0
    }, 5, 5);

    @Override
    public GrayImage get() {
        return image;
    }
}
