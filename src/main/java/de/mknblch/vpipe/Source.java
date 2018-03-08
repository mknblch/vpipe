package de.mknblch.vpipe;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
@FunctionalInterface
public interface Source<T> extends Supplier<T> {

    default <O> Source<O> connectTo(Function<T, O> processor) {
        return () -> processor.apply(Source.this.get());
    }

    static <I, O> Source<O> build(Supplier<I> supplier, Function<I, O> function) {
        return () -> function.apply(supplier.get());
    }
}
