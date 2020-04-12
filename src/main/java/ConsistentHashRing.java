import hash.Hash;
import hash.MD5Hash;
import node.Node;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public final class ConsistentHashRing<T extends Node> implements ConsistentHash<T> {

    private final ConcurrentSkipListMap<Integer, T> ring = new ConcurrentSkipListMap<>();
    private final Hash hash;

    public ConsistentHashRing() {
        this(new MD5Hash());
    }

    public ConsistentHashRing(Hash hash) {
        this.hash = hash;
    }

    @Override
    public boolean addNode(T node) {
        int slot = hash.hash(node.getKey());
        ring.put(slot, node);
        return true;
    }

    @Override
    public boolean addNodes(Collection<T> nodes) {
        return false;
    }

    @Override
    public boolean containsNode(T node) {
        return false;
    }

    @Override
    public boolean removeNode(T node) {
        return false;
    }

    @Override
    public Stream<T> getNodes() {
        return null;
    }

    @Override
    public T route(String key) {
        return null;
    }
}
