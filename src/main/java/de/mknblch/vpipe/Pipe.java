package de.mknblch.vpipe;

import de.mknblch.vpipe.model.Processor;


/**
 * @author mknblch
 */
public class Pipe<I, O> {

    private final Processor<I, ?> head;
    private final Processor<?, O> tail;

    public Pipe(Processor<I, ?> head, Processor<?, O> tail) {
        this.head = head;
        this.tail = tail;
    }

    public <O2> Pipe<I, O2> add(Processor<O, O2> processor) {
        return new Pipe<>(
                head,
                tail.connectTo(processor)
        );
    }
}
