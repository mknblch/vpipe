package de.mknblch.vpipe.functions.contours;

import java.util.List;
import java.util.function.Function;

/**
 * The processor approximates if a contour is enclosed
 * by another one and assigns them accordingly.
 *
 * The resulting list is reused during processing and
 * should not be altered.
 *
 * @author mknblch
 */
public class Hash implements Function<List<Contour>, List<Contour>> {

    private static final double CIRCULARITY_THRESHOLD = 0.005;

    private final int p;

    public Hash(int p) {
        this.p = p;
    }

    @Override
    public List<Contour> apply(List<Contour> contours) {
        contours.forEach(this::computeHash);
        return contours;
    }

    private void computeHash(Contour contour) {
        if (contour.hash != -1) {
            return;
        }
        if (contour.isLeaf()) {
//            if (contour.circularity() > CIRCULARITY_THRESHOLD) {
//                return;
//            }
            contour.hash = p;
            return;
        }
        contour.hash = p;
        contour.forEachChild(c -> {
            computeHash(c);
            contour.hash += c.hash;
        });
        contour.hash *= p;
    }
}
