package de.mknblch.contours;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

/**
 * @author mknblch
 */
public class BufferedImageRenderer {

    private BufferedImage out = null;

    private static BufferedImage createMonochromImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    }

    private static BufferedImage createColorImage(int width, int height) {

        final byte[] pixels = new byte[width * height * 3];
        final DataBuffer dataBuffer = new DataBufferByte(pixels, width*height, 0);
        final ComponentSampleModel smodel = new ComponentSampleModel(
                DataBuffer.TYPE_BYTE,
                width,
                height,
                3,
                width * 3,
                new int[]{0, 1, 2});
        final ComponentColorModel cmodel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[]{8, 8, 8},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        final WritableRaster raster = Raster.createWritableRaster(
                smodel,
                dataBuffer,
                null);

        return new BufferedImage(cmodel, raster, false, null);
    }

    public BufferedImage render(Image image) {
        return render(
                image.data(),
                image.width(),
                image.height(),
                image.channels());
    }

    public BufferedImage render(byte[] data,
                                int width, int height, Image.Channels channels) {
        if (null == out) {
            out = createColorImage(width, height);
        }
        final byte[] imageData;
        switch (channels) {
            case COLOR:
            imageData = ((DataBufferByte) out
                        .getRaster()
                        .getDataBuffer())
                        .getData();
                break;

            default:
                imageData = ((DataBufferByte) out
                        .getRaster()
                        .getDataBuffer())
                        .getData();
                break;
        }

        System.arraycopy(data, 0, imageData, 0, data.length);
        return out;
    }

}
