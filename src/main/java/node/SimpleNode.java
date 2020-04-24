package node;

import annotation.Generated;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleNode)) return false;
        SimpleNode that = (SimpleNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @Generated
    public String toString() {
        return value;
    }
}
