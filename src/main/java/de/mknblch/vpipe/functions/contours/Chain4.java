package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.I;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.E;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.N;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.S;

/**
 * @author mknblch
 */
public class Chain4 implements Function<Image.Gray, List<Contour>> {

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

    public Chain4(int threshold) {
        this(threshold, (perimeter, signedArea, x0, y0, x1, y1) -> true);
    }

    public Chain4(int threshold, Contour.Filter contourFilter) {
        this(threshold, contourFilter, 640 * 480 * 4);
    }

    public Chain4(int threshold, Contour.Filter contourFilter, int initialCapacity) {
        builder = new Contour.Builder(contourFilter, initialCapacity);
        this.threshold = threshold;
    }

    @Override
    public List<Contour> apply(Image.Gray image) {
        this.input = image.data;
        this.width = image.width;
        this.height = image.height;
        if (null == visited || visited.length < image.pixels()) {
            visited = new boolean[(image.width + 1) * (image.height + 1)];
        }
        Arrays.fill(visited, false);
        builder.reset();
        final List<Contour> contours = new ArrayList<>();
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
                if (c == null) {
                    continue;
                }
                append(contours, c);
            }
        }
        return contours;
    }

    private boolean append(List<Contour> contours, Contour c) {
        for (int j = contours.size() - 1; j >= 0; j--) {
            final Contour previous = contours.get(j);
            if (previous.approximateContains(c)) {
                previous.children.add(c);
                previous.dx += previous.cx() - c.cx();
                previous.dy += previous.cy() - c.cy();
                c.parent = previous;
                c.level = previous.level + 1;
                contours.add(c);
                return true;
            }
        }
        contours.add(c);
        return false;
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