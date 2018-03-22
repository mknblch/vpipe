package de.mknblch.vpipe.functions.contours;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Contour {

    public static final double PI2 = Math.pow(Math.PI, 2);

    public enum Direction {
        E(0), S(1), W(2), N(3);
        public final byte v;
        Direction(int v) {
            this.v = (byte) v;
        }
    }

    private final byte[] data;
    private final int offset;
    private final int length;

    final List<Contour> children = new ArrayList<>();
    int dx, dy;
    Contour parent;
    int level;
    int hash = -1;

    public final int x;
    public final int y;
    public final int minX;
    public final int maxX;
    public final int minY;
    public final int maxY;
    public final int signedArea;


    private Contour(byte[] data,
            int offset,
            int length,
            int x,
            int y,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int signedArea) {
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.signedArea = signedArea;
    }

    /**
     *
     * @return
     */
    public Contour parent() {
        return parent;
    }

    /**
     * @return list of child contours (contours enclosed by this one)
     */
    public List<Contour> children() {
        return children;
    }

    /**
     * center of the bounding box
     * @return x ordinate of the center of the bounding box
     */
    public int cx() {
        return minX + width() / 2;
    }

    /**
     * center of the bounding box
     * @return y ordinate of the center of the bounding box
     */
    public int cy() {
        return minY + height() / 2;
    }

    /**
     * angle of the contour based on it children
     * @return angle in radians
     */
    public double angle() {
        if (children.isEmpty()) {
            return .0;
        }
        return Math.atan2(dy, dx);
    }

    /**
     * area of the contour  (including the area of its children)
     * @return unsigned area
     */
    public int area() {
        return Math.abs(signedArea);
    }

    /**
     * @return width of the minimal enclosing bounding box
     */
    public int width() {
        return maxX - minX;
    }

    /**
     * @return height of the minimal enclosing bounding box
     */
    public int height() {
        return maxY - minY;
    }

    /**
     * perimeter of the contour
     * @return contour perimeter
     */
    public int perimeter() {
        return data.length + 1;
    }

    /**
     * depth of the node in the tree
     * @return distance from root node
     */
    public int level() {
        return level;
    }

    /**
     *
     * @return
     */
    public int maxLevel() {
        if (children.isEmpty()) {
            return 0;
        }
        int d = 0;
        for (Contour child : children) {
            d = Math.max(d, child.maxLevel());
        }
        return d + 1;
    }

    /**
     * transform contour into a {@link Polygon}
     * @return a polygon
     */
    public Polygon toPolygon() {
        final Polygon polygon = new Polygon();
        forEach(polygon::addPoint);
        return polygon;
    }

    public int depth() {
        if (null == parent) {
            return 0;
        }
        return parent.depth() + 1;
    }

    public double circularity() {
        final double optArea = width() / 2 * PI2;
        return (area() / optArea) / Math.PI;
    }

    /**
     *
     * @return
     */
    public int hash() {
        return hash;
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

    /**
     * iterate the contour.
     *
     * point data is only valid in the during the computation
     * of the actual frame.
     * @param consumer consumer impl
     */
    public void forEach(PointConsumer consumer) {
        forEach(consumer, 1);
    }

    /**
     * iterate the contour.
     *
     * point data is only valid in the during the computation
     * of the actual frame.
     * @param consumer consumer impl
     * @param stepSize
     */
    public void forEach(PointConsumer consumer, int stepSize) {
        int tx = x, ty = y + 1;
        consumer.consume(tx, ty);
        int i = 0;
        for (; i < length; i++) {
            switch (data[i + offset]) {
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
            if (i % stepSize == 0) {
                consumer.consume(tx, ty);
            }
        }
    }

    /**
     * get copy of the crack data. only valid during
     * computation of the actual frame.
     */
    public byte[] data() {
        return Arrays.copyOfRange(data, offset, offset + length);
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
         * @param signedArea signed area (inner contour if area < 0, outer contour otherwise)
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
    static class Builder {

        private byte[] data;
        private int offset;
        private int index;
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;
        private int signedArea;
        private int lx, ly;
        private int sx, sy;

        /**
         * @param initialCapacity initial data capacity (grows by 1/3 if full)
         */
        Builder(int initialCapacity) {
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
         * build and finalize the current contour
         * @return a Contour
         */
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
