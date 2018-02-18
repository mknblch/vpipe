package de.mknblch.contours;

/**
 * @author mknblch
 */
public class Chain<I, O> extends Processor<I, O> {

    @Override
    public O compute(I in) {

        return null;
    }

    @Override
    public <O2> Processor<O, O2> connectTo(Processor<O, O2> nextProcessor) {

        return nextProcessor;

    }
}
