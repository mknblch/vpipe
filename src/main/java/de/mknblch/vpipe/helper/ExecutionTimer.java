package de.mknblch.vpipe.helper;

import java.util.function.Function;

/**
 * @author mknblch
 */
public class ExecutionTimer<I, O> implements Function<I, O> {

    private final Function<I, O> function;
    private final int steps;
    private int n = 0;
    private long startTime = 0;
    private long duration;
    private Listener listener = (function, duration) -> {
        System.out.printf("%s ~%s%n",
                function.getClass().getSimpleName(),
                String.format("%d.%04ds (%d fps)", (duration / 1000) % 60, duration, 1000 / duration));
    };

    public interface Listener {

        void call(Function<?, ?> function, long duration);
    }

    public ExecutionTimer(Function<I, O> function, int steps) {
        this.function = function;
        if (steps <= 0) {
            throw new IllegalArgumentException("Illegal number of steps");
        }
        this.steps = steps;
    }

    public ExecutionTimer<I, O> withListener(Listener listener) {
        this.listener = listener;
        return this;
    }


    private void end() {
        duration = System.currentTimeMillis() - startTime;
        listener.call(function, (duration / steps) + 1);
    }

    @Override
    public O apply(I i) {
        if (n++ == 0) {
            startTime = System.currentTimeMillis();
        }
        final O apply = function.apply(i);
        if (n >= steps) {
            end();
            n = 0;
        }
        return apply;
    }

}
