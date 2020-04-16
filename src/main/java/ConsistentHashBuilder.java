import hash.Hasher;
import hash.Murmur3Hasher;
import node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ConsistentHashBuilder<T extends Node> {

    private String name = "";
    private Hasher hasher;
    private int replicationFactor = 10;
    private Collection<T> nodes = Collections.emptyList();

    private ConsistentHashBuilder() {
    }

    public static <T extends Node> ConsistentHashBuilder<T> newBuilder() {
        return new ConsistentHashBuilder<>();
    }

    public ConsistentHashBuilder<T>  name(String name) {
        assertNotNull(name, "Name can not be null");
        this.name = name;
        return this;
    }

    public ConsistentHashBuilder<T> hasher(Hasher hasher) {
        this.hasher = hasher;
        return this;
    }

    public ConsistentHashBuilder<T> replicationFactor(int replicationFactor) {
        if (replicationFactor < 1) {
            throw new IllegalArgumentException("Replication Factor can not be less than 1");
        }
        this.replicationFactor = replicationFactor;
        return this;
    }

    public ConsistentHashBuilder<T> nodes(Collection<T> nodes) {
        assertNotNull(nodes, "Nodes list can not be null");
        this.nodes = nodes;
        return this;
    }

    public ConsistentHash<T> build() {
        hasher = hasher != null ? hasher : new Murmur3Hasher();
        ConsistentHashRing<T> ring = new ConsistentHashRing<T>(name, hasher, replicationFactor);
        ring.addAll(new ArrayList<>(nodes));
        return ring;
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
