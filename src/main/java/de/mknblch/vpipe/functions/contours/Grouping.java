package de.mknblch.vpipe.functions.contours;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author mknblch
 */
public class Grouping implements Function<List<Contour>, List<Contour>> {

    private final boolean includeChildren;
    private final List<Contour> out;
    private final BiPredicate<Contour, Contour> predicate;

    public Grouping() {
        this(false, false);
    }

    public Grouping(boolean exactGrouping, boolean excludeChildren) {
        this.includeChildren = !excludeChildren;
        this.out = new ArrayList<>();
        this.predicate = exactGrouping ?
                (outer, inner) ->
                        inner.isWhite() ^ outer.isWhite() &&
                        inner.minX >= outer.minX &&
                        inner.maxX <= outer.maxX &&
                        inner.minY >= outer.minY &&
                        inner.maxY <= outer.maxY :
                (outer, inner) ->
                        inner.minX >= outer.minX &&
                        inner.maxX <= outer.maxX &&
                        inner.minY >= outer.minY &&
                        inner.maxY <= outer.maxY;

    }

    @Override
    public List<Contour> apply(List<Contour> contours) {
        out.clear();
        outer:
        for (int i = 0; i < contours.size(); i++) {
            final Contour c = contours.get(i);
            for (int j = i - 1; j >= 0; j--) {
                final Contour previous = contours.get(j);
                if (predicate.test(previous, c)) {
                    previous.children.add(c);
                    previous.dx += previous.cx() - c.cx();
                    previous.dy += previous.cy() - c.cy();
                    c.parent = previous;
                    c.level = previous.level + 1;
                    if (includeChildren) {
                        out.add(c);
                    }
                    continue outer;
                }
            }
            out.add(c);
        }
        return out;
    }
}
