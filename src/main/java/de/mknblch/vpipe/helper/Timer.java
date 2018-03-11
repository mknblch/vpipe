package de.mknblch.vpipe.helper;

import java.util.function.Function;

/**
 * @author mknblch
 */
public class Timer<I, O> implements Function<I, O> {

    private final Function<I, O> function;
    private final int steps;

    private int step = 0;
    private long startTime = 0;

    private long duration;

    public Timer(Function<I, O> function, int steps) {
        this.function = function;
        this.steps = steps;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        final long millis = System.currentTimeMillis();
        step++;
        duration += millis - startTime;
        if (step >= steps) {
            System.out.printf("%s ~%s%n",
                    function.getClass().getSimpleName(),
                    format(duration / steps));
            step = 0;
            duration = 0;
        }

    }

    public String format(long duration) {
        return String.format("%d.%04ds", (duration / 1000) % 60, duration);
    }

    @Override
    public O apply(I i) {
        start();
        final O apply = function.apply(i);
        end();
        return apply;
    }
}
