package de.mknblch.vpipe.model;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mknblch
 */
@FunctionalInterface
public interface Source<T> extends Supplier<T> {

    default <O> Source<O> connectTo(Function<T, O> processor) {
        return () -> processor.apply(get());
    }
}
