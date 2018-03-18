package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.Source;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author mknblch
 */
public class ImageSource implements Source<Image.Color> {

    private final Image.Color colorImage;

    public ImageSource(Path path) throws IOException {
        final BufferedImage bufferedImage;
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            bufferedImage = convert(ImageIO.read(inputStream));
        }
        colorImage = new Image.Color(bufferedImage.getWidth(), bufferedImage.getHeight());

        final DataBufferByte buffer = (DataBufferByte) bufferedImage
                .getRaster()
                .getDataBuffer();
        System.arraycopy(buffer.getData(), 0, colorImage.data, 0, colorImage.data.length);
    }

    public ImageSource(InputStream inputStream) throws IOException {
        final BufferedImage bufferedImage = convert(ImageIO.read(inputStream));
        colorImage = new Image.Color(bufferedImage.getWidth(), bufferedImage.getHeight());
        final DataBufferByte buffer = (DataBufferByte) bufferedImage
                .getRaster()
                .getDataBuffer();
        System.arraycopy(buffer.getData(), 0, colorImage.data, 0, colorImage.data.length);
    }

    private static BufferedImage convert(BufferedImage in) {
        BufferedImage temp = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        temp.getGraphics().drawImage(in, 0, 0, null);
        return temp;
    }

    @Override
    public Image.Color get() {
        return colorImage;
    }
}
