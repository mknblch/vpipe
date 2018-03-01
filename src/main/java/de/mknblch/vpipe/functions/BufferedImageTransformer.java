package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.Image;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.function.Function;

/**
 * @author mknblch
 */
public class BufferedImageTransformer<I extends Image> implements Function<I, BufferedImage> {

    private BufferedImage out = null;

    @Override
    public BufferedImage apply(I image) {
        return render(
                image.data(),
                image.width(),
                image.height(),
                image instanceof Image.Color);
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
                new int[]{Image.RED,
                        Image.GREEN,
                        Image.BLUE});
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

    private static BufferedImage createMonoImage(int width, int height) {
        final byte[] pixels = new byte[width * height * 3];
        final DataBuffer dataBuffer = new DataBufferByte(pixels, width*height, 0);
        final ComponentSampleModel smodel = new ComponentSampleModel(
                DataBuffer.TYPE_BYTE,
                width,
                height,
                1,
                width,
                new int[]{Image.RED});
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

    private BufferedImage render(byte[] data, int width, int height, boolean color) {
        if (null == out) {
            if (color) {
                out = createColorImage(width, height);
            } else {
                out = createMonoImage(width, height);
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
