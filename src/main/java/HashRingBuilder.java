import hash.DefaultHasher;
import hash.Hasher;
import node.Node;

import java.util.*;

final class HashRingBuilder<T extends Node> {

    private String name;
    private Hasher hash;
    private int partitionRate = 1000;
    private Collection<T> nodes = Collections.emptyList();

    public HashRingBuilder() { }

    public HashRingBuilder<T> name(String name) {
        Objects.requireNonNull(name, "Name can not be null");
        this.name = name;
        return this;
    }

    public HashRingBuilder<T> hasher(Hasher hash) {
        this.hash = hash;
        return this;
    }

    public HashRingBuilder<T> partitionRate(int partitionRate) {
        if (partitionRate < 1) {
            throw new IllegalArgumentException("Replication Factor can not be less than 1");
        }
        this.partitionRate = partitionRate;
        return this;
    }

    public HashRingBuilder<T> nodes(Collection<T> nodes) {
        Objects.requireNonNull(name, "Name can not be null");
        Objects.requireNonNull(nodes, "Nodes list can not be null");
        this.nodes = nodes;
        return this;
    }

    public HashRing<T> build() {
        name = name != null ? name : generateName();
        hash = hash != null ? hash : DefaultHasher.MURMUR_3;

        HashRing<T> ring = new HashRing<>(name, hash, partitionRate);
        ring.addAll(new ArrayList<>(nodes));

        return ring;
    }

    private String generateName() {
        return "hash_ring_" + new Random().nextInt(10_000);
    }
}
