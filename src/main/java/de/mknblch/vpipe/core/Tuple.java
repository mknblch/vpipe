package de.mknblch.vpipe.core;

/**
 * @author Jiří Kraml (jkraml@avantgarde-labs.de)
 */
public class Tuple {

    private Tuple() {}

    public static class Two<L, R> {

        private final L left;
        private final R right;

        private Two(L left, R right) {
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

    public static class Three<L, M, R> {

        private final L left;
        private final M middle;
        private final R right;

        private Three(L left, M middle, R right) {
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

    public static <L, R> Two<L, R> from(L left, R right) {
        return new Two<>(left, right);
    }

    public static <L, M, R> Three<L, M, R> from(L left, M middle, R right) {
        return new Three<>(left, middle, right);
    }

}