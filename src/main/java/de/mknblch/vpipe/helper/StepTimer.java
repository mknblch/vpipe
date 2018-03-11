package de.mknblch.vpipe.helper;

import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author mknblch
 */
public class StepTimer<I, O> implements Function<I, O> {

    private final Function<I, O> function;
    private final int printTimeout;

    private long nextLog = 0;
    private int steps = 0;
    private long startTime = 0;
    private int phase = 0;

    public StepTimer(Function<I, O> function, int printTimeout) {
        this.printTimeout = printTimeout;
        this.function = function;
    }

    public void step() {
        final long millis = System.currentTimeMillis();
        if (steps == 0) {
            startTime = millis;
            nextLog = millis + printTimeout;
        }
        steps++;
        phase++;
        if (millis < nextLog) {
            return;
        }
        nextLog = millis + printTimeout;
        final long duration = millis - startTime;
        System.out.printf("%s #%d, %s, %.0f steps/s%n",
                function.getClass().getSimpleName(),
                steps,
                format(duration),
                phase / (printTimeout / 1000d)
        );
        phase = 0;
    }

    public String format(long duration) {

        final int k = (int) (duration / 1000);
        final int sec = k % 60;
        final int min = (k % 3600) / 60;
        final int hour = k / 3600;

        if (hour > 0) {
            return String.format("%dh:%dm:%ds", hour, min, sec);
        }
        if (min > 0) {
            return String.format("%dm:%ds", min, sec);
        }
        return String.format("%ds", sec);
    }

    @Override
    public O apply(I i) {
        step();
        return function.apply(i);
    }
}
