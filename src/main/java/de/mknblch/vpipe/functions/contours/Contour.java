package de.mknblch.vpipe.functions.contours;

import java.awt.*;
import java.util.ArrayList;
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

    public final byte[] data;
    public final int x;
    public final int y;
    public final int minX;
    public final int maxX;
    public final int minY;
    public final int maxY;
    public final int id;
    public final int signedArea;
    public final List<Contour> children = new ArrayList<>();

    public final Polygon polygon;

    Contour parent;
    int level;


    Contour(byte[] data, int x, int y, int minX, int maxX, int minY, int maxY, int signedArea, int id, Polygon polygon) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.signedArea = signedArea;
        this.id = id;
        this.polygon = polygon;
    }

    /**
     * @param other possible child contour
     * @return true if this contour encloses the given one
     */
    public boolean encloses(Contour other) {
//        final Polygon polygon = this.toPolygon();
//        return polygon.contains(other.minX, other.minY) &&
//                polygon.contains(other.maxX, other.maxY);
        return other.isWhite() != isWhite() &&
                other.minX >= this.minX &&
                other.maxX <= this.maxX &&
                other.minY >= this.minY &&
                other.maxY <= this.maxY;
    }

    public Contour getParent() {
        return parent;
    }

    public List<Contour> getChildren() {
        return children;
    }

    public int cx() {
        return minX + (maxX - minX) / 2;
    }

    public int cy() {
        return minY + (maxY - minY) / 2;
    }

    public double getAngle() {
        if (children.isEmpty()) {
            return .0;
        }
        final int cx = cx();
        final int cy = cy();
        int bx = cx;
        int by = cy;
        for (Contour child : children) {
            by += cy - child.cy();
            bx += cx - child.cx();
        }
        return Math.atan2(by - cy, bx - cx);
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
//        final Polygon polygon = new Polygon();
//        forEach((x, y) -> polygon.addPoint(x, y));
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
        consumer.consume(x, y);
        int tx = x, ty = y + 1;
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
    }

    public interface PointConsumer {
        void consume(int x, int y);
    }
}
