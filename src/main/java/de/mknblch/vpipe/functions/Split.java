package de.mknblch.vpipe.functions;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Split {

    private Split() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static class SplitTwo<I, L, R> implements Function<I, TupleTwo<L, R>> {
        private final Function<I, L> leftProcessor;
        private final Function<I, R> rightProcessor;

        public SplitTwo(Function<I, L> leftProcessor, Function<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcess must not be null");
            requireNonNull(rightProcessor, "rightProcess must not be null");

            this.leftProcessor = leftProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleTwo<L, R> apply(I in) {
            return new TupleTwo<>(
                    leftProcessor.apply(in),
                    rightProcessor.apply(in)
            );
        }
    }

    public static class SplitThree<I, L, M, R> implements Function<I, TupleThree<L, M, R>> {
        private final Function<I, L> leftProcessor;
        private final Function<I, M> middleProcessor;
        private final Function<I, R> rightProcessor;

        public SplitThree(Function<I, L> leftProcessor, Function<I, M> middleProcessor, Function<I, R> rightProcessor) {
            requireNonNull(leftProcessor, "leftProcess must not be null");
            requireNonNull(middleProcessor, "middleProcess must not be null");
            requireNonNull(rightProcessor, "rightProcess must not be null");

            this.leftProcessor = leftProcessor;
            this.middleProcessor = middleProcessor;
            this.rightProcessor = rightProcessor;
        }

        @Override
        public TupleThree<L, M, R> apply(I in) {
            return new TupleThree<>(
                    leftProcessor.apply(in),
                    middleProcessor.apply(in),
                    rightProcessor.apply(in)
            );
        }
    }
}
