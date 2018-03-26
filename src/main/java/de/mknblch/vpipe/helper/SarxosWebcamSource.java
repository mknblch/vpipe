package de.mknblch.vpipe.helper;

import com.github.sarxos.webcam.Webcam;
import de.mknblch.vpipe.core.Image;
import de.mknblch.vpipe.core.Source;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author mknblch
 */
public class SarxosWebcamSource implements Source<Image.Color>, AutoCloseable {

    private final Webcam webcam;
    private final Image.Color image;

    public SarxosWebcamSource(Webcam webcam, int width, int height) {
        this.webcam = webcam;
        webcam.setViewSize(new Dimension(width, height));
        if (!webcam.open()) {
            throw new IllegalStateException("Cannot open Webcam");
        }
        image = new Image.Color(width, height);
    }

    @Override
    public void close() {
        webcam.close();
    }

    @Override
    public Image.Color get() {
        webcam.getImageBytes().get(image.data);
        return image;
    }

    public static SarxosWebcamSource choose() {
        return choose(null);
    }

    public static SarxosWebcamSource choose(String name) {
        Webcam webcam;
        final List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.size() == 1) {
            return new SarxosWebcamSource(webcams.get(0), 640, 480);
        }

        if ((webcam = find(webcams, name)) != null) {
            return new SarxosWebcamSource(webcam, 640, 480);
        }

        final Object[] array = webcams.toArray();
        webcam = (Webcam) JOptionPane.showInputDialog(null,
                "Choose cam",
                "Cam:",
                JOptionPane.QUESTION_MESSAGE,
                null,
                array,
                array[0]);
        if (null == webcam) {
            System.exit(0);
        }

        printViewSizes(webcam);

        return new SarxosWebcamSource(webcam, 640, 480);
    }

    private static void printViewSizes(Webcam webcam) {
        final Dimension[] viewSizes = webcam.getViewSizes();
        final StringBuilder builder = new StringBuilder();
        for (Dimension viewSize : viewSizes) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(String.format("[%d, %d]", viewSize.width, viewSize.height));
        }
        builder.insert(0, "webcam.getViewSizes() = ");
        System.out.println(builder.toString());
    }

    private static Webcam find(List<Webcam> webcams, String name) {
        if (null == name) {
            return null;
        }
        for (Webcam webcam : webcams) {
            if (webcam.getName().toLowerCase().contains(name.toLowerCase())) {
                return webcam;
            }
        }
        return null;
    }

}
