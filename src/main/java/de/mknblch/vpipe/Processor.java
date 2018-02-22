package de.mknblch.vpipe;

/**
 * @author mknblch
 */
public abstract class Processor<I, O> {

    private Processor<?, I> previous;

    public O pull() {
        return compute(previous.pull());
    }

    public abstract O compute(I in);

    public <O2> Processor<O, O2> connectTo(Processor<O, O2> nextProcessor) {
        nextProcessor.previous = this;
        return nextProcessor;
    }
}
