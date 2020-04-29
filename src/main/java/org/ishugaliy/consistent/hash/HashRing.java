package org.ishugaliy.consistent.hash;

import org.ishugaliy.consistent.hash.annotation.Generated;
import org.ishugaliy.consistent.hash.hasher.Hasher;
import org.ishugaliy.consistent.hash.node.Node;
import org.ishugaliy.consistent.hash.partition.Partition;
import org.ishugaliy.consistent.hash.partition.ReplicationPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public final class HashRing<T extends Node> implements ConsistentHash<T> {

    private static final Logger LOG = LoggerFactory.getLogger(HashRing.class);

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);
    private final Map<T, Set<Partition<T>>> nodes = new HashMap<>();
    private final NavigableMap<Long, Partition<T>> ring = new TreeMap<>();

    private final String name;
    private final Hasher hasher;
    private final int partitionRate;

    HashRing(String name, Hasher hasher, int partitionRate) {
        this.name = name;
        this.hasher = hasher;
        this.partitionRate = partitionRate;
    }

    public static <T extends Node> HashRingBuilder<T> newBuilder() {
        return new HashRingBuilder<>();
    }

    @Override
    public boolean add(T node) {
        mutex.writeLock().lock();
        try {
            return addNode(node);
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public boolean addAll(Collection<T> nodes) {
        mutex.writeLock().lock();
        try {
            if (nodes == null) {
                nodes = emptyList();
            }
            nodes = nodes.stream()
                    .filter(this::addNode)
                    .collect(Collectors.toList());
            return !nodes.isEmpty();
        } finally {
            mutex.writeLock().unlock();
        }
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
        Optional<T> node = Optional.empty();
        try {
            if (key != null) {
                node = findNodes(key, 1).stream().findAny();
            }
        } finally {
            mutex.readLock().unlock();
        }
        return node;
    }

    @Override
    public Set<T> locate(String key, int count) {
        mutex.readLock().lock();
        Set<T> nodes = emptySet();
        try {
            if (key != null && count > 0) {
                nodes = findNodes(key, count);
            }
        } finally {
            mutex.readLock().unlock();
        }
        return nodes;
    }

    @Override
    public String getName() {
        return name;
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

    public Hasher getHasher() {
        return hasher;
    }

    public int getPartitionRate() {
        return partitionRate;
    }

    private boolean addNode(T node) {
        boolean added = false;
        if (node != null && !nodes.containsKey(node)) {
            Set<Partition<T>> partitions = createPartitions(node);
            distributePartitions(partitions);
            nodes.put(node, partitions);
            added = true;
        }
        return added;
    }

    private Set<Partition<T>> createPartitions(T node) {
        return IntStream.range(0, partitionRate)
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

    private Set<T> findNodes(String key, int count) {
        Set<T> res = new HashSet<>();
        if (count < nodes.size()) {
            long slot = hash(key);
            Iterator<Partition<T>> it = new ClockwiseIterator(slot);
            while (it.hasNext() && res.size() < count) {
                Partition<T> part = it.next();
                res.add(part.getNode());
            }
        } else {
            res.addAll(nodes.keySet());
        }
        return res;
    }

    private class ClockwiseIterator implements Iterator<Partition<T>> {
        private final Iterator<Partition<T>> head;
        private final Iterator<Partition<T>> tail;

        public ClockwiseIterator(long slot) {
            this.head = ring.headMap(slot).values().iterator();
            this.tail = ring.tailMap(slot).values().iterator();
        }

        @Override
        public boolean hasNext() {
            return head.hasNext() || tail.hasNext();
        }

        @Override
        public Partition<T> next() {
            return tail.hasNext() ? tail.next() : head.next();
        }
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", HashRing.class.getSimpleName() + "[", "]")
                .add("nodes= " + nodes.size())
                .add("name= '" + name + "'")
                .add("hasher= " + hasher)
                .add("partitionRate= " + partitionRate)
                .toString();
    }
}
