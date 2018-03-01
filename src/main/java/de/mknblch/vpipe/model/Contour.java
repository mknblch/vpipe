package de.mknblch.vpipe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    public final int index;

    public final List<Contour> children = new ArrayList<>();

    private int depth = 0;

    public Contour(byte[] data, int x, int y, int minX, int maxX, int minY, int maxY, int index) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.index = index;
    }

    public boolean contains(Contour other) {
        return other.minX >= this.minX &&
                other.maxX <= this.maxX &&
                other.minY >= this.minY &&
                other.maxY <= this.maxY;
    }

    public void add(Contour child) {
        children.add(child);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isOuter() {
        return depth % 2 == 0;
    }

    public void forEachChild(Consumer<Contour> consumer) {
        children.forEach(consumer);
    }

    public void forEach(PointConsumer consumer) {
        int tx = x, ty = y;
        for (int i = 0; i < data.length; i++) {
            consumer.consume(tx, ty);
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
        }
//        consumer.consume(tx, ty);
    }

    public interface PointConsumer {
        void consume(int x, int y);
    }
}
