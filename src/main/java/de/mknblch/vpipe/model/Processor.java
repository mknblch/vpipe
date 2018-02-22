package de.mknblch.vpipe.model;

/**
 * @author mknblch
 */
public abstract class Processor<I, O> {

    protected Processor<?, I> previous;
    protected Processor<O, ?> next;

    public O pull() {
        return compute(previous.pull());
    }

    public abstract O compute(I in);

    public <O2> Processor<O, O2> connectTo(Processor<O, O2> nextProcessor) {
        this.next = nextProcessor;
        nextProcessor.previous = this;
        return nextProcessor;
    }
}
