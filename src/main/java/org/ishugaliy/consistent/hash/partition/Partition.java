package org.ishugaliy.consistent.hash.partition;

import org.ishugaliy.consistent.hash.node.Node;

public interface Partition<T extends Node> {

    T getNode();

    long getSlot();

    void setSlot(long slot);

    String getPartitionKey();
}