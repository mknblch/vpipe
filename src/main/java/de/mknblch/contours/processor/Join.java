package de.mknblch.contours.processor;

import de.mknblch.contours.Processor;
import de.mknblch.contours.processor.Parallelize.TupleThree;
import de.mknblch.contours.processor.Parallelize.TupleTwo;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Join {

    private Join() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static <L, R> Processor<Void, TupleTwo<L, R>> join(Processor<?, L> leftProcessor, Processor<?, R> rightProcessor) {
        return new JoinTwo<>(leftProcessor, rightProcessor);
    }

    public static <L, M, R> Processor<Void, TupleThree<L, M, R>> join(Processor<?, L> leftProcessor, Processor<?, M> middleProcessor, Processor<?, R> rightProcessor) {
        return new JoinThree<>(leftProcessor, middleProcessor, rightProcessor);
    }

    public static class JoinTwo<L, R> extends Processor<Void, TupleTwo<L, R>> {
        private final Processor<?, L> leftProcessor;
        private final Processor<?, R> rightProcessor;

        public JoinTwo(Processor<?, L> leftProcessor, Processor<?, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcessor must not be null");
            requireNonNull(rightProcessor, "rightProcessor must not be null");

            this.leftProcessor = leftProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleTwo<L, R> compute(Void in) {
            return new TupleTwo<>(leftProcessor.pull(), rightProcessor.pull());
        }
    }

    public static class JoinThree<L, M, R> extends Processor<Void, TupleThree<L, M, R>> {
        private final Processor<?, L> leftProcessor;
        private final Processor<?, M> middleProcessor;
        private final Processor<?, R> rightProcessor;

        public JoinThree(Processor<?, L> leftProcessor, Processor<?, M> middleProcessor, Processor<?, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcessor must not be null");
            requireNonNull(middleProcessor, "middleProcessor must not be null");
            requireNonNull(rightProcessor, "rightProcessor must not be null");

            this.leftProcessor = leftProcessor;
            this.middleProcessor = middleProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleThree<L, M, R> compute(Void in) {
            return new TupleThree<>(leftProcessor.pull(), middleProcessor.pull(), rightProcessor.pull());
        }
    }
}
