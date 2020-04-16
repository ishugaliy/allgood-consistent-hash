package node;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class SimpleNode implements Node {

    private final String value;

    public SimpleNode(String value) {
        this.value = value;
    }

    @Override
    public String getPartitionKey() {
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
}
