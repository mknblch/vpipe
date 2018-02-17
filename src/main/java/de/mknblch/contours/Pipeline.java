package de.mknblch.contours;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mknblch
 */
public class Pipeline implements AutoCloseable, VideoSource {

    private final VideoSource videoSource;
    private final Processor[] processors;

    public Pipeline(VideoSource videoSource, Processor[] processors) {
        this.videoSource = videoSource;
        this.processors = processors;
    }

    @Override
    public Image image() {
        Image image = videoSource.image();
        for (int i = 0; i < processors.length; i++) {
            image = processors[i].compute(image);
        }
        return image;
    }

    @Override
    public void close() throws Exception {
        videoSource.close();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int fps() {
        return videoSource.fps();
    }

    public static class Builder {

        private final List<Processor> processors;

        private Builder() {
            processors = new ArrayList<>();
        }

        public Builder add(Processor processor) {
            processors.add(processor);
            return this;
        }

        public Pipeline build(VideoSource source) {
            final Processor[] processors = this.processors.toArray(new Processor[]{});
            return new Pipeline(source, processors);
        }

        public Pipeline build() {
            final Processor[] processors = this.processors.toArray(new Processor[]{});
            return new Pipeline(new DefaultVideoSource(), processors);
        }
    }
}
