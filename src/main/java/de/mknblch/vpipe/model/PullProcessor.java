package de.mknblch.vpipe.model;

import java.util.function.Supplier;

/**
 * @author mknblch
 */
public class PullProcessor<I, O> implements Supplier<O> {

    private final Supplier<I> source;
    private final Process<I, O> func;

    private PullProcessor(Supplier<I> source, Process<I, O> func) {
        this.source = source;
        this.func = func;
    }

    @Override
    public O get() {
        return func.compute(source.get());
    }

    public <O2> PullProcessor<O, O2> connectTo(Process<O, O2> process) {
        return new PullProcessor<>(this, process);
    }

    public static <V> PullProcessor<V, V> from(Supplier<V> supplier) {
        return new PullProcessor<>(supplier, Process.identity());
    }
}
