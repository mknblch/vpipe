package de.mknblch.vpipe.model;

/**
 *
 * @author mknblch
 */
@FunctionalInterface
public interface Processor<I, O> {

    O compute(I in);

    default <O2> Processor<I, O2> connectTo(Processor<? super O, ? extends O2> next) {
        return (I in) -> next.compute(compute(in));
    }

    static <T> Processor<T, T> identity() {
        return t -> t;
    }
}
