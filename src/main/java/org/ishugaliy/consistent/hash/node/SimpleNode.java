package org.ishugaliy.consistent.hash.node;

import org.ishugaliy.consistent.hash.annotation.Generated;

import java.util.Objects;

public class SimpleNode implements Node {

    private final String value;

    private SimpleNode(String value) {
        Objects.requireNonNull(value, "Value can not be null");
        this.value = value;
    }

    public static SimpleNode of(String value) {
        return new SimpleNode(value);
    }

    @Override
    public String getKey() {
        return value;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleNode)) return false;
        SimpleNode that = (SimpleNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @Generated
    public String toString() {
        return value;
    }
}
