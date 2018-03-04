package de.mknblch.vpipe.helper;

import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.Source;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author mknblch
 */
public class ImageSource implements Source<Image.Color> {

    private final Path path;
    private final BufferedImage bufferedImage;
    private final Image.Color colorImage;

    public ImageSource(Path path) throws IOException {
        this.path = path;
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            bufferedImage = ImageIO.read(inputStream);
        }
        colorImage = new Image.Color(bufferedImage.getWidth(), bufferedImage.getHeight());

        final DataBufferByte buffer = (DataBufferByte) bufferedImage
                .getRaster()
                .getDataBuffer();
        System.arraycopy(buffer.getData(), 0, colorImage.data, 0, colorImage.data.length);
    }

    @Override
    public Image.Color get() {
        return colorImage;
    }
}
