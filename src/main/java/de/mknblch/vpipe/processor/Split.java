package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.Process;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Split {

    private Split() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static <I, L, R> Process<I, TupleTwo<L, R>> split(Process<I, L> leftProcess, Process<I, R> rightProcess) {
        return new SplitTwo<>(leftProcess, rightProcess);
    }

    public static <I, L, M, R> Process<I, TupleThree<L, M, R>> split(Process<I, L> leftProcess, Process<I, M> middleProcess, Process<I, R> rightProcess) {
        return new SplitThree<>(leftProcess, middleProcess, rightProcess);
    }

    public static class NoOpProcess<I> implements Process<I, I> {
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

    public static class SplitTwo<I, L, R> implements Process<I, TupleTwo<L, R>> {
        private final Process<I, L> leftProcess;
        private final Process<I, R> rightProcess;

        public SplitTwo(Process<I, L> leftProcess, Process<I, R> rightProcess) {
            requireNonNull(leftProcess, "leftProcess must not be null");
            requireNonNull(rightProcess, "rightProcess must not be null");

            this.leftProcess = leftProcess;
            this.rightProcess = rightProcess;
        }

        @Override
        public TupleTwo<L, R> compute(I in) {
            return new TupleTwo<>(
                    leftProcess.compute(in),
                    rightProcess.compute(in)
            );
        }
    }

    public static class SplitThree<I, L, M, R> implements Process<I, TupleThree<L, M, R>> {
        private final Process<I, L> leftProcess;
        private final Process<I, M> middleProcess;
        private final Process<I, R> rightProcess;

        public SplitThree(Process<I, L> leftProcess, Process<I, M> middleProcess, Process<I, R> rightProcess) {
            requireNonNull(leftProcess, "leftProcess must not be null");
            requireNonNull(middleProcess, "middleProcess must not be null");
            requireNonNull(rightProcess, "rightProcess must not be null");

            this.leftProcess = leftProcess;
            this.middleProcess = middleProcess;
            this.rightProcess = rightProcess;
        }

        @Override
        public TupleThree<L, M, R> compute(I in) {
            return new TupleThree<>(
                    leftProcess.compute(in),
                    middleProcess.compute(in),
                    rightProcess.compute(in)
            );
        }
    }
}
