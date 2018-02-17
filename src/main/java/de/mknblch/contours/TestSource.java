package de.mknblch.contours;

/**
 * @author mknblch
 */
public class TestSource extends Processor<Void, Image> implements VideoSource {

    private final Image image = new Image(new byte[]{
            0, 0, 0, 0, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, (byte) 255, 0, (byte) 255, 0,
            0, (byte) 255, (byte) 255, (byte) 255, 0,
            0, 0, 0, 0, 0
    }, 5, 5, Image.Type.MONOCHROM);

    @Override
    public Image image() {
        return image;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Image pull() {
        return image;
    }

    @Override
    public Image compute(Void in) {
        return null;
    }
}
