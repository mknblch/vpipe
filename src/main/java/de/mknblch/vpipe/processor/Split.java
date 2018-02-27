package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.Processor;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Split {

    private Split() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static class NoOpProcessor<I> implements Processor<I, I> {
        @Override
        public I compute(I in) {
            return in;
        }
    }

    public static class TupleTwo<L, R> {
        private final L left;
        private final R right;

        public TupleTwo(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }

    public static class TupleThree<L, M, R> {
        private final L left;
        private final M middle;
        private final R right;

        public TupleThree(L left, M middle, R right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public M getMiddle() {
            return middle;
        }

        public R getRight() {
            return right;
        }
    }

    public static class SplitTwo<I, L, R> implements Processor<I, TupleTwo<L, R>> {
        private final Processor<I, L> leftProcessor;
        private final Processor<I, R> rightProcessor;

        public SplitTwo(Processor<I, L> leftProcessor, Processor<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcess must not be null");
            requireNonNull(rightProcessor, "rightProcess must not be null");

            this.leftProcessor = leftProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleTwo<L, R> compute(I in) {
            return new TupleTwo<>(
                    leftProcessor.compute(in),
                    rightProcessor.compute(in)
            );
        }
    }

    public static class SplitThree<I, L, M, R> implements Processor<I, TupleThree<L, M, R>> {
        private final Processor<I, L> leftProcessor;
        private final Processor<I, M> middleProcessor;
        private final Processor<I, R> rightProcessor;

        public SplitThree(Processor<I, L> leftProcessor, Processor<I, M> middleProcessor, Processor<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcess must not be null");
            requireNonNull(middleProcessor, "middleProcess must not be null");
            requireNonNull(rightProcessor, "rightProcess must not be null");

            this.leftProcessor = leftProcessor;
            this.middleProcessor = middleProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleThree<L, M, R> compute(I in) {
            return new TupleThree<>(
                    leftProcessor.compute(in),
                    middleProcessor.compute(in),
                    rightProcessor.compute(in)
            );
        }
    }
}
