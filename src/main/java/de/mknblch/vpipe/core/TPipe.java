package de.mknblch.vpipe.core;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author mknblch
 */
public class TPipe<T> implements Function<T, T> {

    private final Consumer<T> observer;

    public TPipe(Consumer<T> observer) {
        this.observer = observer;
    }

    @Override
    public T apply(T t) {
        observer.accept(t);
        return t;
    }
}
