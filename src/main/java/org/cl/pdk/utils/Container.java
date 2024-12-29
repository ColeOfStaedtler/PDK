package org.cl.pdk.utils;

public final class Container<T> {
    private T element;

    public Container(T element) {
        this.element = element;
    }

    public Container() {
        this.element = null;
    }

    public boolean isEmpty() {
        return element == null;
    }

    public T get() {
        return element;
    }

    public void set(T element) {
        this.element = element;
    }
}
