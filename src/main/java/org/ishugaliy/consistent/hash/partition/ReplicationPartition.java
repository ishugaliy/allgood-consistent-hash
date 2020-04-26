package org.ishugaliy.consistent.hash.partition;

import org.ishugaliy.consistent.hash.annotation.Generated;
import org.ishugaliy.consistent.hash.node.Node;

import java.util.Objects;
import java.util.StringJoiner;

public final class ReplicationPartition<T extends Node> implements Partition<T> {

    private final int index;
    private final T node;
    private long slot;

    public ReplicationPartition(int index, T node) {
        Objects.requireNonNull(node, "Node can not be null");
        this.index = index;
        this.node = node;
    }

    public ReplicationPartition(int index, T node, long slot) {
        this(index, node);
        this.slot = slot;
    }

    public int getIndex() {
        return index;
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
    @Generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicationPartition)) return false;
        ReplicationPartition<?> that = (ReplicationPartition<?>) o;
        return index == that.index &&
                slot == that.slot &&
                Objects.equals(node, that.node);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(index, node, slot);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ReplicationPartition.class.getSimpleName() + "[", "]")
                .add("index=" + index)
                .add("key=" + getPartitionKey())
                .add("slot=" + slot)
                .toString();
    }
}
