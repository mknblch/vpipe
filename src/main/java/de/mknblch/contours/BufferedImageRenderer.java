package de.mknblch.contours;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * @author mknblch
 */
public class BufferedImageRenderer {

    public BufferedImage render(Image image) {
        return render(image.data(), image.width(), image.height());
    }

    public BufferedImage render(int[] data, int width, int height) {
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final int[] imageData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(data, 0, imageData, 0, data.length);
        return img;
    }

}
