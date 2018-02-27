package de.mknblch.vpipe;

import de.mknblch.vpipe.model.Processor;


/**
 * @author mknblch
 */
public class Chain<I, O> extends Processor<I, O> {

    private final Processor<I, ?> head;
    private final Processor<?, O> tail;

    public Chain(Processor<I, ?> head, Processor<?, O> tail) {
        this.head = head;
        this.tail = tail;
    }

    public <O2> Chain<I, O2> add(Processor<O, O2> processor) {
        return new Chain<>(
                head,
                tail.connectTo(processor)
        );
    }

    public static <I, O> Chain<I, O> head(Processor<I, O> processor) {
        return new Chain<>(processor, processor);
    }

    @Override
    public <O2> Processor<O, O2> connectTo(Processor<O, O2> nextProcessor) {
        return tail.connectTo(nextProcessor);
    }

    @Override
    public O compute(I in) {



        return null;
    }
}
