package de.mknblch.contours.processor;

import de.mknblch.contours.Processor;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Parallelize {

    private Parallelize() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static <I, L, R> Processor<I, TupleTwo<L, R>> parallel(Processor<I, L> leftProcessor, Processor<I, R> rightProcessor) {
        return new ParallelTwo<>(leftProcessor, rightProcessor);
    }

    public static <I, L, M, R> Processor<I, TupleThree<L, M, R>> parallel(Processor<I, L> leftProcessor, Processor<I, M> middleProcessor, Processor<I, R> rightProcessor) {
        return new ParallelThree<>(leftProcessor, middleProcessor, rightProcessor);
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

    public static class ParallelTwo<I, L, R> extends Processor<I, TupleTwo<L, R>> {
        private final Processor<I, L> leftProcessor;
        private final Processor<I, R> rightProcessor;

        public ParallelTwo(Processor<I, L> leftProcessor, Processor<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcessor must not be null");
            requireNonNull(rightProcessor, "rightProcessor must not be null");

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

    public static class ParallelThree<I, L, M, R> extends Processor<I, TupleThree<L, M, R>> {
        private final Processor<I, L> leftProcessor;
        private final Processor<I, M> middleProcessor;
        private final Processor<I, R> rightProcessor;

        public ParallelThree(Processor<I, L> leftProcessor, Processor<I, M> middleProcessor, Processor<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcessor must not be null");
            requireNonNull(middleProcessor, "middleProcessor must not be null");
            requireNonNull(rightProcessor, "rightProcessor must not be null");

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