package de.mknblch.vpipe.model;

/**
 *
 * @author mknblch
 */
@FunctionalInterface
public interface Process<I, O> {

    O compute(I in);

    default <O2> Process<I, O2> connectTo(Process<? super O, ? extends O2> next) {
        return (I t) -> next.compute(compute(t));
    }
}
