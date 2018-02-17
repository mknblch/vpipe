package de.mknblch.contours;

/**
 * @author mknblch
 */
public abstract class Processor<I, O> {

    private Processor<?, I> previous;

    public O pull() {
        return compute(previous.pull());
    }

    public abstract O compute(I in);

    public <I> Processor<O, I> connectTo(Processor<O, I> nextProcessor) {
        nextProcessor.previous = this;
        return nextProcessor;
    }
}
