package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.I;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.E;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.S;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.N;

/**
 * Crack-code based image segmentation.
 *
 * The processor scans an image for values which exceec the
 * threshold and starts to crawl around the blob until it
 * reaches it's origin. During this process properties like
 * perimeter, bounding box and signed area are computed.
 *
 * @author mknblch
 */
public class Chain4 implements Function<Image.Gray, List<Contour>> {

    private final Contour.Filter contourFilter;
    private final Contour.Builder builder;

    private final int threshold;
    private Contour.Direction d;
    private boolean[] visited;
    private byte[] input;
    private int width;
    private int height;
    private int j;
    private int x;
    private int y;

    private final List<Contour> contours = new ArrayList<>();

    public Chain4(int threshold) {
        this(threshold, (perimeter, signedArea, x0, y0, x1, y1) -> true);
    }

    public Chain4(int threshold, Contour.Filter contourFilter) {
        this(threshold, contourFilter, 800 * 600 * 4);
    }

    public Chain4(int threshold, Contour.Filter contourFilter, int initialCapacity) {
        this.contourFilter = contourFilter;
        builder = new Contour.Builder(initialCapacity);
        this.threshold = threshold;
    }

    @Override
    public List<Contour> apply(Image.Gray image) {
        this.input = image.data;
        this.width = image.width;
        this.height = image.height;
        final int length = image.length();
        if (null == visited || visited.length < length) {
            visited = new boolean[length];
        }
        Arrays.fill(visited, false);
        contours.clear();
        builder.reset();
        for (int i = 0; i < image.data.length - image.width - 1; i++) {
            if (visited[i]) {
                continue;
            }
            final int sx = i % image.width;
            if (sx == image.width - 1) {
                continue;
            }
            final int sy = i / image.width;
            if ((sx == 0 || threshold > I(image.data[i - 1])) && threshold < I(image.data[i])) {
                final Contour c = chain4(i, sx, sy);
                if (contourFilter.test(c.perimeter(), c.signedArea, c.minX, c.minY, c.maxX, c.maxY)) {
                    contours.add(c);
                }
            }
        }
        return contours;
    }

    private Contour chain4(int i, int sx, int sy) {
        x = sx;
        y = sy + 1;
        j = i + width;
        d = S;
        builder.create(sx, sy);
        do {
            switch (d) {
                case E:
                    east();
                    break;
                case S:
                    south();
                    break;
                case W:
                    west();
                    break;
                case N:
                    north();
                    break;
            }
            builder.add(d.v, x, y);
        } while (i != j);
        return builder.build();
    }

    private void east() {
        if (x == width - 1 || threshold > I(input[j - width])) {
            visited[j] = true;
            d = N;
            y--;
            j -= width;
        } else if (y == height - 1 || threshold > I(input[j])) {
            d = E;
            x++;
            j++;
        } else {
            visited[j] = true;
            d = S;
            y++;
            j += width;
        }
    }

    private void south() {
        if (y == height - 1 || threshold > I(input[j])) {
            d = E;
            x++;
            j++;
        } else if (x <= 0 || threshold > I(input[j - 1])) {
            visited[j] = true;
            d = S;
            y++;
            j += width;
        } else {
            d = Contour.Direction.W;
            x--;
            j--;
        }
    }

    private void west() {
        if (x <= 0 || threshold > I(input[j - 1])) {
            visited[j] = true;
            d = S;
            y++;
            j += width;
        } else if (y <= 0 || threshold > I(input[j - width - 1])) {
            d = Contour.Direction.W;
            x--;
            j--;
        } else {
            visited[j] = true;
            d = N;
            y--;
            j -= width;
        }
    }

    private void north() {
        if (y <= 0 || threshold > I(input[j - width - 1])) {
            d = Contour.Direction.W;
            x--;
            j--;
        } else if (x == width - 1 || threshold > I(input[j - width])) {
            visited[j] = true;
            d = N;
            y--;
            j -= width;
        } else {
            d = E;
            x++;
            j++;
        }
    }

}
