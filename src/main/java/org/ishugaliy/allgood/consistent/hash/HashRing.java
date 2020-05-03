/*
 * The MIT License
 *
 * Copyright (c) 2020 Iurii Shugalii
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

package org.ishugaliy.allgood.consistent.hash;

import org.ishugaliy.allgood.consistent.hash.annotation.Generated;
import org.ishugaliy.allgood.consistent.hash.hasher.Hasher;
import org.ishugaliy.allgood.consistent.hash.node.Node;
import org.ishugaliy.allgood.consistent.hash.partition.Partition;
import org.ishugaliy.allgood.consistent.hash.partition.ReplicationPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;

/**
 * Implementation of Consistent Hash Ring with Virtual Nodes (partitions).
 * Hash ring is based on Binary Search Tree - {@link TreeMap}
 *
 * @see <a href="https://docs.datastax.com/en/dse/6.8/dse-arch/datastax_enterprise/dbArch/archDataDistributeHashing.html"/>
 *
 * @param <T> the type of node to be used in the ring
 *
 * @author Iurrii Shugalii
 */
public final class HashRing<T extends Node> implements ConsistentHash<T> {

    private static final Logger LOG = LoggerFactory.getLogger(HashRing.class);

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);
    private final Map<T, Set<Partition<T>>> nodes = new HashMap<>();
    private final NavigableMap<Long, Partition<T>> ring = new TreeMap<>();

    private final String name;
    private final Hasher hasher;
    private final int partitionRate;


    /**
     * Instance can be create only via builder {@link HashRing#newBuilder()}
     *
     * @param name the name of the hash ring
     * @param hasher the hash function
     * @param partitionRate amount of partitions to be created per node
     */
    HashRing(String name, Hasher hasher, int partitionRate) {
        this.name = name;
        this.hasher = hasher;
        this.partitionRate = partitionRate;
        LOG.info("Ring [{}] created: hasher [{}], partitionRate [{}]", name, hasher, partitionRate);
    }

    /**
     * Create instance of {@link HashRingBuilder}
     *
     * @param <T> the type of node
     * @return the hash ring builder
     */
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
                LOG.info("Ring [{}]: node [{}] removed", name, node);
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
        Optional<T> node;
        try {
            node = findNode(key);
        } finally {
            mutex.readLock().unlock();
        }
        return node;
    }

    @Override
    public Set<T> locate(String key, int count) {
        mutex.readLock().lock();
        Set<T> nodes;
        try {
            nodes = findNodes(key, count);
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
            LOG.info("Ring [{}]: node [{}] added", name, node);
            added = true;
        }
        return added;
    }

    private Set<Partition<T>> createPartitions(T node) {
        Set<Partition<T>> partitions = IntStream.range(0, partitionRate)
                .mapToObj(idx -> new ReplicationPartition<>(idx, node))
                .collect(Collectors.toSet());
        LOG.debug("Ring [{}]: node [{}] partitions created", name, node);
        return partitions;
    }

    private void distributePartitions(Set<Partition<T>> partitions) {
        for (Partition<T> part : partitions) {
            String pk = part.getPartitionKey();
            long slot = findSlot(pk);
            part.setSlot(slot);
            ring.put(slot, part);
            LOG.debug("Ring [{}]: node [{}] partitions distributed", name, part.getNode());
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

    private Optional<T> findNode(String key) {
        return findNodes(key, 1).stream().findAny();
    }

    private Set<T> findNodes(String key, int count) {
        Set<T> res = new HashSet<>();
        if (key != null && count > 0) {
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
        }
        LOG.debug("Ring [{}]: key [{}] located nodes [{}]", name, key, res);
        return res;
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
}
