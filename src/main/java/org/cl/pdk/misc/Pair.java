package org.cl.pdk.misc;

public final class Pair<T, U> {
    private T left;
    private U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public Pair() {
        this(null, null);
    }

    public T left() {
        return left;
    }

    public void left(T left) {
        this.left = left;
    }

    public U right() {
        return right;
    }

    public void right(U right) {
        this.right = right;
    }
}
