package de.mknblch.vpipe.functions;

import java.util.function.Function;
import java.util.function.LongConsumer;

/**
 * @author mknblch
 */
public class ExecutionTimer<I, O> implements Function<I, O> {

    private final Function<I, O> function;
    private final LongConsumer listener;
    private final int steps;
    private int n = 0;
    private long duration;

    public ExecutionTimer(Function<I, O> function, LongConsumer listener, int steps) {
        this.function = function;
        this.listener = listener;
        if (steps <= 0) {
            throw new IllegalArgumentException("Illegal number of steps");
        }
        this.steps = steps;
    }

    @Override
    public O apply(I i) {
        final long startTime = System.nanoTime();
        final O apply = function.apply(i);
        duration += System.nanoTime() - startTime;
        if (n++ == steps) {
            listener.accept(duration / (steps * 1_000_000) + 1);
            n = 0;
            duration = 0;
        }
        return apply;
    }
}
