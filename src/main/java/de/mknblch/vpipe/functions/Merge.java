package de.mknblch.vpipe.functions;

import de.mknblch.vpipe.functions.Split.TupleThree;
import de.mknblch.vpipe.functions.Split.TupleTwo;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;


/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Merge {

    private Merge() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    @FunctionalInterface
    public interface TriFunction<L, M, R, O> {
        O apply(L left, M middle, R right);
    }

    public static class MergeTwo<L, R, O> implements Function<TupleTwo<L, R>, O> {

        private final BiFunction<L, R, O> mergeFunktion;

        public MergeTwo(BiFunction<L, R, O> mergeFunktion) {
            requireNonNull(mergeFunktion, "mergeFunktion must not be null");

            this.mergeFunktion = mergeFunktion;
        }

        @Override
        public O apply(TupleTwo<L, R> in) {
            return mergeFunktion.apply(in.getLeft(), in.getRight());
        }
    }

    public static class MergeThree<L, M, R, O> implements Function<TupleThree<L, M, R>, O> {

        private final TriFunction<L, M, R, O> mergeFunction;

        public MergeThree(TriFunction<L, M, R, O> mergeFunction) {
            requireNonNull(mergeFunction, "mergeFunction must not be null");

            this.mergeFunction = mergeFunction;
        }

        @Override
        public O apply(TupleThree<L, M, R> in) {
            return mergeFunction.apply(in.getLeft(), in.getMiddle(), in.getRight());
        }
    }


}
