package de.mknblch.vpipe.processor;

import de.mknblch.vpipe.model.Processor;
import de.mknblch.vpipe.processor.Split.TupleThree;
import de.mknblch.vpipe.processor.Split.TupleTwo;

import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;


/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Merge {

    private Merge() {
        throw new UnsupportedOperationException("no instantiation allowed");
    }

    public static <L, R, O> Processor<TupleTwo<L, R>, O> merge(BiFunction<L, R, O> mergeFunction) {
        return new MergeTwo<>(mergeFunction);
    }

    public static <L, M, R, O> Processor<TupleThree<L, M, R>, O> merge(TriFunction<L, M, R, O> mergeFunction) {
        return new MergeThree<>(mergeFunction);
    }

    @FunctionalInterface
    public interface TriFunction<L, M, R, O> {
        O apply(L left, M middle, R right);
    }

    public static class MergeTwo<L, R, O> extends Processor<TupleTwo<L, R>, O> {

        private final BiFunction<L, R, O> mergeFunktion;

        public MergeTwo(BiFunction<L, R, O> mergeFunktion) {
            requireNonNull(mergeFunktion, "mergeFunktion must not be null");

            this.mergeFunktion = mergeFunktion;
        }

        @Override
        public O compute(TupleTwo<L, R> in) {
            return mergeFunktion.apply(in.getLeft(), in.getRight());
        }
    }

    public static class MergeThree<L, M, R, O> extends Processor<TupleThree<L, M, R>, O> {

        private final TriFunction<L, M, R, O> mergeFunction;

        public MergeThree(TriFunction<L, M, R, O> mergeFunction) {
            requireNonNull(mergeFunction, "mergeFunction must not be null");

            this.mergeFunction = mergeFunction;
        }

        @Override
        public O compute(TupleThree<L, M, R> in) {
            return mergeFunction.apply(in.getLeft(), in.getMiddle(), in.getRight());
        }
    }


}
