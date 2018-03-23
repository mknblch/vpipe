package de.mknblch.vpipe;

import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

/**
 * @author mknblch
 */
public class HSVTest {

    @Test
    public void test() throws Exception {

        float[] hsb = new float[3];
        Color.RGBtoHSB(0, 0,0, hsb);
        System.out.println("Arrays.toString(hsb) = " + Arrays.toString(hsb));

    }
}
