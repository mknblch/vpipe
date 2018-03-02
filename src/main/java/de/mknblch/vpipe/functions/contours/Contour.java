package de.mknblch.vpipe.functions.contours;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Contour {

    public enum Direction {
        EAST(0), SOUTH(1), WEST(2), NORD(3);
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

    Contour parent;
    int depth = 0;
    int hash;


    Contour(byte[] data, int x, int y, int minX, int maxX, int minY, int maxY, int index) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.index = index;
    }

    /**
     * @param other possible child contour
     * @return true if this contour encloses the given one
     */
    public boolean encloses(Contour other) {
        return other.minX >= this.minX &&
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
    public int getDepth() {
        return depth;
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
        return depth % 2 == 0;
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
