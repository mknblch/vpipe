package de.mknblch.contours;

/**
 * @author mknblch
 */
public interface Image {

    enum Component {

        RED(0), GREEN(1),BLUE(2);

        public final int value;

        Component(int value) {
            this.value = value;
        }
    }

    enum Channels {

        COLOR(3), MONOCHROM(1);

        public final int channels;

        Channels(int channels) {
            this.channels = channels;
        }
    }

    byte[] data();

    int width();

    int height();

    Channels channels();

    default byte getValue(int x, int y) {
        final Channels channels = channels();
        return data()[y * width() * channels.channels + x * channels.channels];
    }

    default byte getValue(int x, int y, Component colorComponent) {
        final Channels channels = channels();
        return data()[y * width() * channels.channels + x * channels.channels + colorComponent.value];
    }

    default void setValue(int x, int y, byte value) {
        final Channels channels = channels();
        final byte[] data = data();
        data[y * width() * channels.channels + x * channels.channels] = value;
        data[y * width() * channels.channels + x * channels.channels+1] = value;
        data[y * width() * channels.channels + x * channels.channels+2] = value;
    }

    default void setValue(int offset, byte value) {
        final byte[] data = data();
        data[offset] = value;
        data[offset + 1] = value;
        data[offset + 2] = value;
    }
}
