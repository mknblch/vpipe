package de.mknblch.vpipe;

import de.mknblch.vpipe.model.GrayImage;
import de.mknblch.vpipe.model.Image;
import de.mknblch.vpipe.model.Processor;
import de.mknblch.vpipe.model.VideoSource;

/**
 * @author mknblch
 */
public class TestSource extends Processor<Void, GrayImage> implements VideoSource {

    private final GrayImage image = new GrayImage(new byte[]{
            0, 0, 0, 0, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, (byte) 255, 0, (byte) 255, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, 0, 0, 0, 0
    }, 5, 5);

    @Override
    public Image image() {
        return image;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public GrayImage pull() {
        return image;
    }

    @Override
    public GrayImage compute(Void in) {
        return null;
    }
}
