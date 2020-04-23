package node;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class StringNode implements Node {

    private final String value;

    private StringNode(String value) {
        this.value = value;
    }

    public static StringNode of(String value) {
        return new StringNode(value);
    }

    @Override
    public String getKey() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringNode)) return false;
        StringNode that = (StringNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
}
