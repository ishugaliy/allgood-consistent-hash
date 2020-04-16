package partition;

import node.Node;

public interface Partition<T extends Node> {

    T getNode();

    long getSlot();

    void setSlot(long slot);

    String getPartitionKey();
}