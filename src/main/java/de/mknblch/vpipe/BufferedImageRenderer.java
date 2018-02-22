package de.mknblch.vpipe;

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
                new int[]{ColorImage.RED,
                        ColorImage.GREEN,
                        ColorImage.BLUE});
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


    private static BufferedImage createMono2Image(int width, int height) {

        final byte[] pixels = new byte[width * height * 3];
        final DataBuffer dataBuffer = new DataBufferByte(pixels, width*height, 0);
        final ComponentSampleModel smodel = new ComponentSampleModel(
                DataBuffer.TYPE_BYTE,
                width,
                height,
                1,
                width,
                new int[]{ColorImage.RED});
        final ComponentColorModel cmodel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{8},
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
    private static BufferedImage createMono3Image(int width, int height) {

        final byte[] pixels = new byte[width * height * 3];
        final DataBuffer dataBuffer = new DataBufferByte(pixels, width*height, 0);
        final ComponentSampleModel smodel = new ComponentSampleModel(
                DataBuffer.TYPE_BYTE,
                width,
                height,
                1,
                width,
                new int[]{ColorImage.RED});
        final ComponentColorModel cmodel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{8},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        final WritableRaster raster = Raster.createWritableRaster(
                smodel,
                dataBuffer,
                null);

        return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    }

    public BufferedImage render(Image image) {
        return render(
                image.data(),
                image.width(),
                image.height(),
                image instanceof ColorImage);
    }

    public BufferedImage render(byte[] data,
                                int width, int height, boolean color) {
        if (null == out) {
            if (color) {
                out = createColorImage(width, height);
            } else {
                out = createMono3Image(width, height);
            }
        }
        final byte[] imageData = ((DataBufferByte) out
                .getRaster()
                .getDataBuffer())
                .getData();
        System.arraycopy(data, 0, imageData, 0, data.length);

        return out;
    }

}
