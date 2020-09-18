/*
 * The MIT License
 *
 * Copyright (c) 2020 Yuriy Shugaliy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ishugaliy.allgood.consistent.hash.partition;

import org.ishugaliy.allgood.consistent.hash.annotation.Generated;
import org.ishugaliy.allgood.consistent.hash.node.Node;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Implementation of replicated partitions (virtual nodes).
 * Replication is based on assigning of replication index {@link ReplicationPartition#index}
 * for each partition, that extends the {@link Node#getKey()}.
 *
 * @param <T> the type of the node to be replicated
 *
 * @author Yuriy Shugaliy
 */
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
