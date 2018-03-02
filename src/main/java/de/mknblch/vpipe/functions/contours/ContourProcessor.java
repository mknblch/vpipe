package de.mknblch.vpipe.functions.contours;

import de.mknblch.vpipe.Image;

import java.util.*;
import java.util.function.Function;

import static de.mknblch.vpipe.Image.I;
import static de.mknblch.vpipe.functions.contours.Contour.Direction.*;

/**
 * @author mknblch
 */
public class ContourProcessor implements Function<Image.Gray, List<Contour>> {

    private static final int CAPACITY_LIMIT = 640 * 480 * 4;
    private static final int INITIAL_CAPACITY = 64;

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

        hash(contours);

        return contours;
    }

    private void hash(List<Contour> contours) {
        for (Contour contour : contours) {
            contour.hash = hash(contour);
        }
    }

    private int hash(Contour contour) {
        if (contour.isLeaf()) {
            return 1;
        }
        int[] sum = {1};
        contour.forEachChild(c -> {
            sum[0] += hash(c);
        });
        return sum[0] << 4;
    }

    private boolean append(List<Contour> contours, Contour c) {
        for (int j = contours.size() - 1; j >= 0; j--) {
            final Contour previous = contours.get(j);
            if (previous.encloses(c)) {
                previous.children.add(c);
                c.parent = previous;
                c.depth = previous.depth + 1;
                contours.add(c);
                return true;
            }
        }
        contours.add(c);
        return false;
    }

    private Contour chain4(Image.Gray image, int i, int sx, int sy, int index) {
        final byte[] input = image.data;
        final int width = image.width;
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        Contour.Direction d = SOUTH;
        int x = sx, y = sy + 1;
        int j = i + width;
        buffer.reset();
        do {
            switch (d) {
                case EAST:
                    if (x == width - 1 || threshold > I(input[j - width])) {
                        visited[j] = true;
                        d = NORD;
                        y--;
                        j -= width;
                    } else if (y == image.height - 1 || threshold > I(input[j])) {
                        d = EAST;
                        x++;
                        j++;
                    } else {
                        visited[j] = true;
                        d = SOUTH;
                        y++;
                        j += width;
                    }
                    break;
                case SOUTH:
                    if (y == image.height - 1 || threshold > I(input[j])) {
                        d = EAST;
                        x++;
                        j++;
                    } else if (x <= 0 || threshold > I(input[j - 1])) {
                        visited[j] = true;
                        d = SOUTH;
                        y++;
                        j += width;
                    } else {
                        d = WEST;
                        x--;
                        j--;
                    }
                    break;
                case WEST:
                    if (x <= 0 || threshold > I(input[j - 1])) {
                        visited[j] = true;
                        d = SOUTH;
                        y++;
                        j += width;
                    } else if (y <= 0 || threshold > I(input[j - width - 1])) {
                        d = WEST;
                        x--;
                        j--;
                    } else {
                        visited[j] = true;
                        d = NORD;
                        y--;
                        j -= width;
                    }
                    break;
                case NORD:
                    if (y <= 0 || threshold > I(input[j - width - 1])) {
                        d = WEST;
                        x--;
                        j--;
                    } else if (x == width - 1 || threshold > I(input[j - width])) {
                        visited[j] = true;
                        d = NORD;
                        y--;
                        j -= width;
                    } else {
                        d = EAST;
                        x++;
                        j++;
                    }
                    break;
            }
            minX = x < minX ? x : minX;
            maxX = x > maxX ? x : maxX;
            minY = y < minY ? y : minY;
            maxY = y > maxY ? y : maxY;
        } while (buffer.add(d) && i != j);

        return buffer.offset >= minPerimeter ?
                new Contour(
                        buffer.get(),
                        sx,
                        sy,
                        minX,
                        maxX,
                        minY,
                        maxY,
                        index
                ) : null;
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
