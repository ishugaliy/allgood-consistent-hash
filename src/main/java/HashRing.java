import hash.Hasher;
import node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import partition.Partition;
import partition.ReplicationPartition;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class HashRing<T extends Node> implements ConsistentHash<T> {

    private static final Logger LOG = LoggerFactory.getLogger(HashRing.class);

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);
    private final Map<T, Set<Partition<T>>> nodes = new HashMap<>();
    private final NavigableMap<Long, Partition<T>> ring = new TreeMap<>();

    private final String name;
    private final Hasher hash;
    private final int partitionFactor;

    HashRing(String name, Hasher hash, int partitionFactor) {
        this.name = name;
        this.hash = hash;
        this.partitionFactor = partitionFactor;
    }

    public static <T extends Node> HashRingBuilder<T> newBuilder() {
        return new HashRingBuilder<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean add(T node) {
        mutex.writeLock().lock();
        boolean added = false;
        try {
            if (!nodes.containsKey(node)) {
                Set<Partition<T>> partitions = createPartitions(node);
                distributePartitions(partitions);
                nodes.put(node, partitions);
                added = true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return added;
    }

    @Override
    public boolean addAll(Collection<T> nodes) {
        mutex.writeLock().lock();
        boolean added = false;
        try {
            nodes = nodes.stream()
                    .filter(n -> !this.nodes.containsKey(n))
                    .collect(Collectors.toSet());
            for (T node : nodes) {
                Set<Partition<T>> partitions = createPartitions(node);
                distributePartitions(partitions);
                this.nodes.put(node, partitions);
            }
            if (!nodes.isEmpty()) {
                added = true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return added;
    }

    @Override
    public boolean contains(T node) {
        mutex.readLock().lock();
        try {
            return nodes.containsKey(node);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public boolean remove(T node) {
        mutex.writeLock().lock();
        boolean removed = false;
        try {
            if (nodes.containsKey(node)) {
                Set<Partition<T>> partitions = nodes.remove(node);
                partitions.forEach(p -> ring.remove(p.getSlot()));
                removed = true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return removed;
    }

    @Override
    public Set<T> getNodes() {
        mutex.readLock().lock();
        try {
            return new HashSet<>(nodes.keySet());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<T> locate(String key) {
        mutex.readLock().lock();
        try {
            long slot = hash(key);
            return locatePartition(slot)
                    .map(Partition::getNode);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Set<T> locate(String partitionKey, int count) {
        mutex.readLock().lock();
        Set<T> res = new HashSet<>();
        try {
            if (count >= nodes.size()) {
                return new HashSet<>(nodes.keySet());
            }
            long slot = hash(partitionKey);
            res.addAll(findNodes(ring.tailMap(slot), count));
            res.addAll(findNodes(ring.headMap(slot), count - res.size()));
        } finally {
            mutex.readLock().unlock();
        }
        return res;
    }

    @Override
    public int size() {
        mutex.readLock().lock();
        try {
            return nodes.size();
        } finally {
            mutex.readLock().unlock();
        }
    }

    private Set<Partition<T>> createPartitions(T node) {
        return IntStream.range(0, partitionFactor)
                .mapToObj(idx -> new ReplicationPartition<>(idx, node))
                .collect(Collectors.toSet());
    }

    private void distributePartitions(Set<Partition<T>> partitions) {
        for (Partition<T> part : partitions) {
            String pk = part.getPartitionKey();
            long slot = findSlot(pk);
            part.setSlot(slot);
            ring.put(slot, part);
        }
    }

    private Optional<Partition<T>> locatePartition(long slot) {
        Map.Entry<Long, Partition<T>> e = ring.ceilingEntry(slot);
        if (e == null) {
            e = ring.firstEntry();
        }
        return Optional.ofNullable(e).map(Map.Entry::getValue);
    }

    private Set<T> findNodes(Map<Long, Partition<T>> seg, int count) {
        return seg.values().stream()
                .map(Partition::getNode)
                .distinct()
                .limit(count)
                .collect(Collectors.toSet());
    }

    private long findSlot(String pk) {
        long slot;
        int seed = 0;
        do {
            slot = hash(pk, seed++);
        } while (ring.containsKey(slot));
        return slot;
    }

    private long hash(String key) {
        return hash(key, 0);
    }

    private long hash(String key, int seed) {
        return Math.abs(hash.hash(key, seed));
    }
}
