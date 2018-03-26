package de.mknblch.vpipe.core;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Merge {

    @FunctionalInterface
    public interface TriFunction<L, M, R, O> {
        O apply(L left, M middle, R right);
    }

    private Merge() {}

    /**
     * merge a 2Tuple (emitted by splitter)
     */
    public static class MergeTwo<L, R, O> implements Function<Tuple.Two<L, R>, O> {

        private final BiFunction<L, R, O> mergeFunktion;

        public MergeTwo(BiFunction<L, R, O> mergeFunktion) {
            requireNonNull(mergeFunktion, "mergeFunktion must not be null");

            this.mergeFunktion = mergeFunktion;
        }

        @Override
        public O apply(Tuple.Two<L, R> in) {
            return mergeFunktion.apply(in.getLeft(), in.getRight());
        }
    }

    /**
     * merge a 3Tuple (emitted by splitter)
     */
    public static class MergeThree<L, M, R, O> implements Function<Tuple.Three<L, M, R>, O> {

        private final TriFunction<L, M, R, O> mergeFunction;

        public MergeThree(TriFunction<L, M, R, O> mergeFunction) {
            requireNonNull(mergeFunction, "mergeFunction must not be null");

            this.mergeFunction = mergeFunction;
        }

        @Override
        public O apply(Tuple.Three<L, M, R> in) {
            return mergeFunction.apply(in.getLeft(), in.getMiddle(), in.getRight());
        }
    }
}
