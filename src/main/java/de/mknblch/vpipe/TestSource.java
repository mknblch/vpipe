package de.mknblch.vpipe;

import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Source;

/**
 * @author mknblch
 */
public class TestSource implements Source<MonoImage> {

    private final MonoImage image = new MonoImage(new byte[]{
            0, 0, 0, 0, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, (byte) 255, 0, (byte) 255, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, 0, 0, 0, 0
    }, 5, 5);

    @Override
    public MonoImage get() {
        return image;
    }
}
