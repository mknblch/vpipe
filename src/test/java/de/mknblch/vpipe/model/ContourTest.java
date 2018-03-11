package de.mknblch.vpipe.model;

import de.mknblch.vpipe.Functions;
import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.contours.Contour;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

/**
 *  0   0   0   0   0
 *  0   1   1   1   0
 *  0   1   0   1   0
 *  0   1   1   1   0
 *  0   0   0   0   0
 *
 *
 * @author mknblch
 */
public class ContourTest {

    @Test
    public void test() throws Exception {

        new TestSource()
                .connectTo(Functions.contours(128, (perimeter, area, x0, y0, x1, y1) -> true))
                .get().forEach(c -> {

            System.out.println("c.perimeter() = " + c.perimeter());
            System.out.println(c.x + "," + c.y);
            c.forEach((x, y) -> System.out.println(" (" + x + "," + y + ")"));
        });

    }
}