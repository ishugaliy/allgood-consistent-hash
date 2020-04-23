import hash.Hasher;
import hash.Murmur3Hasher;
import node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class HashRingBuilder<T extends Node> {

    private String name = "";
    private Hasher hasher;
    private int partitionFactor = 10;
    private Collection<T> nodes = Collections.emptyList();

    public HashRingBuilder() { }

    public HashRingBuilder<T> name(String name) {
        assertNotNull(name, "Name can not be null");
        this.name = name;
        return this;
    }

    public HashRingBuilder<T> hasher(Hasher hasher) {
        this.hasher = hasher;
        return this;
    }

    public HashRingBuilder<T> partitionRate(int partitionFactor) {
        if (partitionFactor < 1) {
            throw new IllegalArgumentException("Replication Factor can not be less than 1");
        }
        this.partitionFactor = partitionFactor;
        return this;
    }

    public HashRingBuilder<T> nodes(Collection<T> nodes) {
        assertNotNull(nodes, "Nodes list can not be null");
        this.nodes = nodes;
        return this;
    }

    public HashRing<T> build() {
        hasher = hasher != null ? hasher : new Murmur3Hasher();
        HashRing<T> ring = new HashRing<>(name, hasher, partitionFactor);
        ring.addAll(new ArrayList<>(nodes));
        return ring;
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
