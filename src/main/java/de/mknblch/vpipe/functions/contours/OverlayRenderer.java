package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Functions;
import de.mknblch.vpipe.Image;
import de.mknblch.vpipe.functions.Tuple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author mknblch
 */
public class OverlayRenderer extends Renderer<Tuple.Two<Image.Color, List<Contour>>> {

    private final Map<Integer, Overlay> hashImageMap;
    private final Function<Image, BufferedImage> converter;

    public OverlayRenderer(int width, int height, Map<Integer, Overlay> hashImageMap) {
        super(width, height);
        this.hashImageMap = hashImageMap;
        converter = Functions.toBufferedImage();
    }

    @Override
    protected void init(Graphics2D graphics) {
        super.init(graphics);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    @Override
    void render(Tuple.Two<Image.Color, List<Contour>> in, Graphics2D graphics) {
        graphics.drawImage(converter.apply(in.getLeft()), 0, 0, null);
        for (Contour contour : in.getRight()) {
            final Overlay overlay = hashImageMap.get(contour.hash());
            final Polygon polygon = contour.toPolygon();
            graphics.setColor(Color.RED);
            graphics.drawPolygon(polygon);
            if (null == overlay) {
                continue;
            }
            final AffineTransform affineTransform = new AffineTransform();
            final double dx = overlay.scale * contour.width() / overlay.width;
            final double dy = overlay.scale * contour.height() / overlay.height;
            final int w = (int) (dx * overlay.width) / 2;
            final int h = (int) (dy * overlay.height) / 2;
            affineTransform.translate(contour.cx() - w, contour.cy() - h);
            affineTransform.rotate(contour.angle() + overlay.angle, w, h);
            affineTransform.scale(dx, dy);
            graphics.drawImage(overlay.image, affineTransform, null);
        }
    }

    public static class Overlay {

        public final BufferedImage image;
        public final int width;
        public final int height;
        public final double angle;
        public final double scale;

        public Overlay(InputStream inputStream) throws IOException {
            this(inputStream, 0, 1);
        }

        public Overlay(InputStream inputStream, double angle, double scale) throws IOException {
            this(ImageIO.read(inputStream), angle, scale);
        }

        public Overlay(BufferedImage image) {
            this(image, 0, 1);
        }

        public Overlay(BufferedImage image, double angle, double scale) {
            Objects.requireNonNull(image);
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.angle = angle;
            this.scale = scale;
        }
    }
}