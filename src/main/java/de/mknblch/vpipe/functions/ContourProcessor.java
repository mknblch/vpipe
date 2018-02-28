package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.model.MonoImage;
import de.mknblch.vpipe.model.Contour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.mknblch.vpipe.model.Image.I;
import static de.mknblch.vpipe.model.Contour.Direction.*;

/**
 * @author mknblch
 */
public class ContourProcessor implements Function<MonoImage, List<Contour>> {

    private static final int CAPACITY_LIMIT = 640 * 480 * 4;
    private static final int MIN_CONTOUR_LENGTH = 16;

    public static final int INITIAL_CAPACITY = 64;

    private final int threshold;
    private boolean[] visited;

    private byte[] data = new byte[INITIAL_CAPACITY];

    public ContourProcessor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public List<Contour> apply(MonoImage image) {
        if (null == visited) {
            visited = new boolean[image.width * image.height];
        } else {
            Arrays.fill(visited, false);
        }
        final ArrayList<Contour> contours = new ArrayList<>();
        for (int i = 0; i < image.data.length - image.width - 1; i++) {
            if (visited[i]) {
                continue;
            }
            final int x = i % image.width;
            if (x == image.width - 1) {
                continue;
            }
            final int y = i / image.width;
            if ((x == 0 || threshold > (image.getValue(x - 1, y))) && threshold < (image.getValue(x, y))) {
                final Contour c = chain4(image, i, x, y);
                if (c == null) {
                    continue;
                }
                contours.add(c);
            }
        }
        return contours;
    }

    private Contour chain4(MonoImage image, int i, int sx, int sy) {
        final byte[] input = image.data;
        final int width = image.width;
        int offset = 0;
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        Contour.Direction d = S;
        int x = sx, y = sy + 1;
        int j = i + width;
        do {
            if (offset > CAPACITY_LIMIT) {
                return null;
            }
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
                        d = W;
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
                        d = W;
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
                        d = W;
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
            if (offset + 1 > data.length) {
                data = Arrays.copyOf(data, offset + offset / 3);
            }

            data[offset++] = d.v;
            minX = x < minX ? x : minX;
            maxX = x > maxX ? x : maxX;
            minY = y < minY ? y : minY;
            maxY = y > maxY ? y : maxY;

        } while (i != j);

        return offset >= MIN_CONTOUR_LENGTH ?
                new Contour(
                        Arrays.copyOf(data, offset),
                        sx,
                        sy,
                        minX,
                        maxX,
                        minY,
                        maxY
                ) : null;
    }
}
