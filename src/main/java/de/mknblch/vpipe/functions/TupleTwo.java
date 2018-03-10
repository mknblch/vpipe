package de.mknblch.vpipe.functions;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class TupleTwo<L, R> {
    
    private final L left;
    private final R right;

    public TupleTwo(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}