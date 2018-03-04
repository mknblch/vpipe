package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.I;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.*;

/**
 * @author mknblch
 */
public class ContourProcessor implements Function<Image.Gray, List<Contour>> {

    private static final int CAPACITY_LIMIT = 640 * 480 * 4;
    private static final int INITIAL_CAPACITY = 64;

    public static final int P = 11;

    private final int threshold;
    private final int minPerimeter;
    private final Buffer buffer;
    private boolean[] visited;

    public ContourProcessor(int threshold, int minPerimeter) {
        this.threshold = threshold;
        this.minPerimeter = minPerimeter;
        buffer = new Buffer();
    }

    @Override
    public List<Contour> apply(Image.Gray image) {
        if (null == visited || visited.length != image.pixels()) {
            visited = new boolean[image.width * image.height];
        } else {
            Arrays.fill(visited, false);
        }
        int index = 0;
        final List<Contour> contours = new ArrayList<>();
        for (int i = 0; i < image.data.length - image.width - 1; i++) {
            if (visited[i]) {
                continue;
            }
            final int x = i % image.width;
            if (x == image.width - 1) {
                continue;
            }
            final int y = i / image.width;
            if ((x == 0 || threshold > I(image.data[i - 1])) && threshold < I(image.data[i])) {
                final Contour c = chain4(image, i, x, y, index++);
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
            if (previous.encloses(c)) {
                previous.children.add(c);
                c.parent = previous;
                c.level = previous.level + 1;
                contours.add(c);
                return true;
            }
        }
        contours.add(c);
        return false;
    }

    private Contour chain4(Image.Gray image, int i, int sx, int sy, int id) {
        final byte[] input = image.data;
        final int width = image.width;
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        Contour.Direction d = S;
        int j = i + width;
        int a = 0;
        int x, xl, y, yl;
        x = xl = sx;
        y = yl = sy;
        buffer.reset();
        Polygon polygon = new Polygon();
        do {
            switch (d) {
                case E:
                    if (x == width - 1 || threshold > I(input[j - width])) {
                        visited[j] = true;
                        d = N;
                        y--;
                        j -= width;
                    } else if (y == image.height - 1 || threshold > I(input[j])) {
                        d = E;
                        x++;
                        j++;
                    } else {
                        visited[j] = true;
                        d = S;
                        y++;
                        j += width;
                    }
                    break;
                case S:
                    if (y == image.height - 1 || threshold > I(input[j])) {
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
                    break;
                case W:
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
                    break;
                case N:
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
                    break;
            }
            minX = x < minX ? x : minX;
            maxX = x > maxX ? x : maxX;
            minY = y < minY ? y : minY;
            maxY = y > maxY ? y : maxY;
            a += xl * y - x * yl;
            yl = y;
            xl = x;
            polygon.addPoint(x, y);
        } while (buffer.add(d) && i != j);
        a += xl * sy - sx * yl;
        return buffer.offset >= minPerimeter ?
                new Contour(
                        buffer.get(),
                        sx,
                        sy,
                        minX,
                        maxX,
                        minY,
                        maxY,
                        a,
                        id,
                        polygon) : null;
    }

    private static class Buffer {

        byte[] data = new byte[INITIAL_CAPACITY];
        int offset;

        public boolean add(Contour.Direction crack) {
            if (offset + 1 > data.length) {
                data = Arrays.copyOf(data, offset + offset / 3);
            }
            data[offset++] = crack.v;
            return offset < CAPACITY_LIMIT;
        }

        byte[] get() {
            return Arrays.copyOf(data, offset);
        }

        void reset() {
            offset = 0;
        }
    }
}
