package de.mknblch.vpipe.model;

/**
 * @author mknblch
 */
public class TestSource implements Source<Image.Gray> {

    private final Image.Gray image = new Image.Gray(new byte[]{
            0, 0, 0, 0, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, (byte) 255, 0, (byte) 255, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, 0, 0, 0, 0
    }, 5, 5);

    @Override
    public Image.Gray get() {
        return image;
    }
}
