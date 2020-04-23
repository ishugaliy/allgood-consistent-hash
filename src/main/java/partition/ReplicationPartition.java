package partition;

import com.google.common.base.MoreObjects;
import node.Node;

import java.util.Objects;

public final class ReplicationPartition<T extends Node> implements Partition<T> {

    private final int index;
    private final T node;
    private long slot;

    public ReplicationPartition(int index, T node) {
        this.index = index;
        this.node = node;
    }

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public long getSlot() {
        return slot;
    }

    @Override
    public void setSlot(long slot) {
        this.slot = slot;
    }

    @Override
    public String getPartitionKey() {
        return String.format("rp%d:%s", index, node.getKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicationPartition)) return false;
        ReplicationPartition<?> that = (ReplicationPartition<?>) o;
        return index == that.index &&
                slot == that.slot &&
                Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, node, slot);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("index", index)
                .add("slot", slot)
                .add("partitionKey", getPartitionKey())
                .toString();
    }
}
