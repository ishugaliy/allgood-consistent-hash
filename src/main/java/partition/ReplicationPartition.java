package partition;

import com.google.common.base.MoreObjects;
import node.Node;

import java.util.Objects;

public final class ReplicationPartition<T extends Node> implements Partition<T> {

    private final int id;
    private final T node;
    private long slot;

    public ReplicationPartition(int id, T node) {
        this.id = id;
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
        return String.format("rp%d:%s", id, node.getPartitionKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicationPartition)) return false;
        ReplicationPartition<?> that = (ReplicationPartition<?>) o;
        return id == that.id &&
                slot == that.slot &&
                Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, node, slot);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("slot", slot)
                .add("partitionKey", getPartitionKey())
                .toString();
    }
}
