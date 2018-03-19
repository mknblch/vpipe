package de.mknblch.vpipe.functions.contours;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Contour {

    private static final int P = 11;

    public enum Direction {
        E(0), S(1), W(2), N(3);
        public final byte v;
        Direction(int v) {
            this.v = (byte) v;
        }
    }

    private final byte[] data;
    public final int index;
    public final int length;
    public final int x;
    public final int y;
    public final int minX;
    public final int maxX;
    public final int minY;
    public final int maxY;
    public final int signedArea;
    public final List<Contour> children = new ArrayList<>();

    int dx, dy;
    Contour parent;
    int level;


    Contour(byte[] data,
            int index,
            int length,
            int x,
            int y,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int signedArea) {
        this.data = data;
        this.index = index;
        this.length = length;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.signedArea = signedArea;
    }

    public Contour getParent() {
        return parent;
    }

    public List<Contour> getChildren() {
        return children;
    }

    public int cx() {
        return minX + width() / 2;
    }

    public int cy() {
        return minY + height() / 2;
    }

    public double getAngle() {
        if (children.isEmpty()) {
            return .0;
        }
        return Math.atan2(dy, dx);
    }

    public int getArea() {
        return Math.abs(signedArea);
    }

    public int width() {
        return maxX - minX;
    }

    public int height() {
        return maxY - minY;
    }

    /**
     * perimeter of the contour
     * actually the count of cracks + 1 since the first
     * crack is always SOUTH
     * @return
     */
    public int perimeter() {
        return data.length + 1;
    }

    /**
     * depth of the node in the tree
     * @return depth
     */
    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        if (children.isEmpty()) {
            return 0;
        }
        int d = 0;
        for (Contour child : children) {
            d = Math.max(d, child.getMaxLevel());
        }
        return d + 1;
    }

    public Polygon toPolygon() {
        final Polygon polygon = new Polygon();
        forEach(polygon::addPoint);
        return polygon;
    }

    public int getDepth() {
        if (null == parent) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    public int hash() {
        if (isLeaf()) {
            return P;
        }
        int[] sum = {P};
        forEachChild(c -> {
            sum[0] += c.hash();
        });
        return sum[0] * P;
    }

    /**
     * leafs are nodes with parent but no children
     * @return true if this is a lead
     */
    public boolean isLeaf() {
        return parent != null && children.isEmpty();
    }

    /**
     * @return true if the contour has no children
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    /**
     * check whether the contour encodes a white blob on
     * black background or black blob on white background
     * @return true if white on black, false if black on white
     */
    public boolean isWhite() {
        return signedArea < 0;
    }

    /**
     * head of the contour tree
     * @return true if the contour has no parent
     */
    public boolean isHead() {
        return parent == null;
    }

    public void forEachChild(Consumer<Contour> consumer) {
        children.forEach(consumer);
    }

    public void forEach(PointConsumer consumer) {
        int tx = x, ty = y + 1;
        consumer.consume(tx, ty);
        for (int i = 0; i < length; i++) {
            switch (data[i + index]) {
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

    /**
     * get copy of the crack data
     */
    public byte[] getData() {
        return Arrays.copyOfRange(data, index, index + length);
    }

    /**
     * consumer interface for point iteration
     */
    public interface PointConsumer {
        void consume(int x, int y);
    }

    /**
     * basic contour filter interface
     */
    public interface Filter {

        /**
         * test if the contour should be added to the result list or nor
         * @param perimeter perimeter of the contour
         * @param signedArea signed area (if <0 it an inner contour, outer contour otherwise)
         * @param x0 top left x ordinate
         * @param y0 top left y ordinate
         * @param x1 bottom right x ordinate
         * @param y1 bottom right x ordinate
         * @return true if the contour is allowed, false otherwise
         */
        boolean test(int perimeter, int signedArea, int x0, int y0, int x1, int y1);
    }

    /**
     * The builder instantiates and grows the data array where all cracks are saved.
     * It also evaluates some properties like the bounding box or area of the blob.
     */
    public static class Builder {

        private byte[] data;
        private int offset;
        private final Filter filter;
        private int index;
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;
        private int signedArea;
        private int lx, ly;
        private int sx, sy;

        /**
         * @param filter contour filter
         * @param initialCapacity initial data capacity (grows by 1/3 if full)
         */
        Builder(Filter filter, int initialCapacity) {
            this.filter = filter;
            data = new byte[initialCapacity];
        }

        /**
         * Reset the builder which invalidates the current data
         */
        void reset() {
            offset = 0;
        }

        /**
         * creates a new contour
         * @param sx start x ordinate
         * @param sy start y ordinate
         */
        void create(int sx, int sy) {
            index = offset;
            signedArea = 0;
            minX = Integer.MAX_VALUE;
            maxX = 0;
            minY = Integer.MAX_VALUE;
            maxY = 0;
            this.sx = lx = sx;
            this.sy = ly = sy;
        }

        /**
         * add a data point
         * @param crack a {@link Contour.Direction} encoding the walk direction
         * @param x current x ordinate
         * @param y current y ordinate
         */
        void add(byte crack, int x, int y) {
            minX = x < minX ? x : minX;
            maxX = x > maxX ? x : maxX;
            minY = y < minY ? y : minY;
            maxY = y > maxY ? y : maxY;
            if (offset + 1 > data.length) {
                data = Arrays.copyOf(data, offset + offset / 3);
            }
            data[offset++] = crack;
            signedArea += lx * y - x * ly;
            lx = x;
            ly = y;
        }

        /**
         * test if the current contour satisfies the filter
         * @return true if the contour is allows, false otherwise
         */
        boolean test() {
            return filter.test(offset - index, signedArea, minX, minY, maxX, maxY);
        }

        Contour build() {
            return new Contour(
                            data,
                            index,
                            offset - index,
                            sx,
                            sy,
                            minX,
                            maxX,
                            minY,
                            maxY,
                            signedArea);
        }
    }
}
