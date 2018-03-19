package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.Image;

import static de.mknblch.vpipe.Image.*;
import static de.mknblch.vpipe.Image.BLUE;

/**
 * @author mknblch
 */
public class Images {

    public static Gray add(Gray a, Gray b) {
        Image.dimensionEqual(a, b);
        final Image.Gray out = new Image.Gray(a);
        for (int i = 0; i < out.pixels(); i++) {
            out.setValue(i, clip(a.getValue(i) + b.getValue(i)));
        }
        return out;
    }

    public static Image.Color add(Image.Color a, Image.Gray b) {
        Image.dimensionEqual(a, b);
        final Image.Color out = new Image.Color(a);
        for (int i = 0; i < out.pixels(); i++) {
            out.setColor(i, RED, clip(a.getColor(i, RED) + b.getValue(i)));
            out.setColor(i, GREEN, clip(a.getColor(i, GREEN) + b.getValue(i)));
            out.setColor(i, BLUE, clip(a.getColor(i, BLUE) + b.getValue(i)));
        }
        return out;
    }

    public static Image.Color add(Image.Color a, Image.Color b) {
        Image.dimensionEqual(a, b);
        final Image.Color out = new Image.Color(a);
        for (int i = 0; i < out.pixels(); i ++) {
            out.setColor(i, RED, clip(a.getColor(i, RED) + b.getColor(i, RED)));
            out.setColor(i, GREEN, clip(a.getColor(i, GREEN) + b.getColor(i, GREEN)));
            out.setColor(i, BLUE, clip(a.getColor(i, BLUE) + b.getColor(i, BLUE)));
        }
        return out;
    }


    public static Image.Gray sub(Image.Gray a, Image.Gray b) {
        Image.dimensionEqual(a, b);
        final Image.Gray out = new Image.Gray(a);
        for (int i = 0; i < out.pixels(); i++) {
            out.setValue(i, clip(a.getValue(i) - b.getValue(i)));
        }
        return out;
    }

    public static Image.Color sub(Image.Color a, Image.Gray b) {
        Image.dimensionEqual(a, b);
        final Image.Color out = new Image.Color(a);
        for (int i = 0; i < out.pixels(); i++) {
            out.setColor(i, RED, clip(a.getColor(i, RED) - b.getValue(i)));
            out.setColor(i, GREEN, clip(a.getColor(i, GREEN) - b.getValue(i)));
            out.setColor(i, BLUE, clip(a.getColor(i, BLUE) - b.getValue(i)));
        }
        return out;
    }

    public static Image.Color sub(Image.Color a, Image.Color b) {
        Image.dimensionEqual(a, b);
        final Image.Color out = new Image.Color(a);
        for (int i = 0; i < out.pixels(); i ++) {
            out.setColor(i, RED, clip(a.getColor(i, RED) - b.getColor(i, RED)));
            out.setColor(i, GREEN, clip(a.getColor(i, GREEN) - b.getColor(i, GREEN)));
            out.setColor(i, BLUE, clip(a.getColor(i, BLUE) - b.getColor(i, BLUE)));
        }
        return out;
    }


}
