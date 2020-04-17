import hash.Hasher;
import node.Node;
import partition.Partition;
import partition.ReplicationPartition;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

final class ConsistentHashRing<T extends Node> implements ConsistentHash<T> {

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);
    private final Map<T, Set<Partition<T>>> members = new HashMap<>();
    private final NavigableMap<Long, Partition<T>> ring = new TreeMap<>();

    private final Hasher hasher;
    private final int replicationFactor;

    ConsistentHashRing(Hasher hasher, int replicationFactor) {
        this.hasher = hasher;
        this.replicationFactor = replicationFactor;
    }

    @Override
    public boolean add(T node) {
        mutex.writeLock().lock();
        boolean added = false;
        try {
            if (!members.containsKey(node)) {
                Set<Partition<T>> partitions = createPartitions(node);
                distributePartitions(partitions);
                members.put(node, partitions);
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
                    .filter(n -> !members.containsKey(n))
                    .collect(Collectors.toSet());

            if (!nodes.isEmpty()) {
                for (T node : nodes) {
                    Set<Partition<T>> partitions = createPartitions(node);
                    distributePartitions(partitions);
                    members.put(node, partitions);
                }
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
            return members.containsKey(node);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public boolean remove(T node) {
        mutex.writeLock().lock();
        boolean removed = false;
        try {
            if (members.containsKey(node)) {
                Set<Partition<T>> partitions = members.remove(node);
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
            return new HashSet<>(members.keySet());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<T> locate(String key) {
        mutex.readLock().lock();
        try {
            long slot = hash(key);
            Map.Entry<Long, Partition<T>> e = ring.ceilingEntry(slot);
            if (e == null) {
                e = ring.firstEntry();
            }
            return Optional.ofNullable(e != null ? e.getValue().getNode() : null);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Set<T> locate(String key, int count) {
        throw new UnsupportedOperationException("`locateN` method is not supported");
    }

    @Override
    public int size() {
        mutex.readLock().lock();
        try {
            return members.size();
        } finally {
            mutex.readLock().unlock();
        }
    }

    private Set<Partition<T>> createPartitions(T node) {
        Set<Partition<T>> partitions = new HashSet<>();
        for (int i = 0; i < replicationFactor; i++) {
            Partition<T> p = new ReplicationPartition<>(i, node);
            partitions.add(p);
        }
        return partitions;
    }

    private void distributePartitions(Set<Partition<T>> partitions) {
        for (Partition<T> part : partitions) {
            String pk = part.getPartitionKey();
            long slot = findSlot(pk);
            part.setSlot(slot);
            ring.put(slot, part);
        }
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
        return Math.abs(hasher.hash(key, seed));
    }
}
