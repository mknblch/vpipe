package de.mknblch.vpipe.model;

import de.mknblch.vpipe.Functions;
import de.mknblch.vpipe.Source;
import de.mknblch.vpipe.functions.contours.Contour;
import org.junit.Test;

import java.util.Collection;

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

        final Source<Collection<Contour>> source = new TestSource()
                .connectTo(Functions.contours(128, 1));

        final Collection<Contour> contours = source.get();

        contours.forEach(c -> {

            System.out.println("c.perimeter() = " + c.perimeter());

            System.out.println(c.x + "," + c.y);

            c.forEach((x, y) -> System.out.println(" (" + x + ","  + y + ")"));

        });

    }
}