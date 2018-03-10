package de.mknblch.vpipe.functions;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class TupleThree<L, M, R> {

    private final L left;
    private final M middle;
    private final R right;

    public TupleThree(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }
}