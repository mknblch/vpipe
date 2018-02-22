package de.mknblch.vpipe.model;

public class Contour {

    public enum Direction {
        E(0), S(1), W(2), N(3);

        public final byte v;
        Direction(int v) {
            this.v = (byte) v;
        }
    }

    public final byte[] data;
    public final int x;
    public final int y;
    public final int minX;
    public final int maxX;
    public final int minY;
    public final int maxY;

    public Contour(byte[] data, int x, int y, int minX, int maxX, int minY, int maxY) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void forEach(PointConsumer consumer) {
        int tx = x, ty = y + 1;
        for (int i = 0; i < data.length; i++) {
            switch (data[i]) {
                case 0:
                    tx++;
                    break;
                case 1:
                    ty++;
                    break;
                case 2:
                    tx--;
                    break;
                case 3:
                    ty--;
                    break;

                default:

                    break;
            }
            consumer.consume(tx, ty);
        }
    }

    public interface PointConsumer {
        void consume(int x, int y);
    }
}
