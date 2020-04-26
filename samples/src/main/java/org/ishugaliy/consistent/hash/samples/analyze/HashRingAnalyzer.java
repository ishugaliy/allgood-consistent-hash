package org.ishugaliy.consistent.hash.samples.analyze;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.Node;

import java.util.*;

public class HashRingAnalyzer<T extends Node> implements ConsistentHash<T> {

    private final HashRing<T> target;
    private final Map<T, Integer> stats = new HashMap<>();

    public HashRingAnalyzer(HashRing<T> ring) {
        this.target = ring;
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public boolean add(T node) {
        stats.putIfAbsent(node, 0);
        return target.add(node);
    }

    @Override
    public boolean addAll(Collection<T> nodes) {
        nodes.forEach(n -> stats.putIfAbsent(n, 0));
        return target.addAll(nodes);
    }

    @Override
    public boolean contains(T node) {
        return target.contains(node);
    }

    @Override
    public boolean remove(T node) {
        stats.remove(node);
        return target.remove(node);
    }

    @Override
    public Set<T> getNodes() {
        return target.getNodes();
    }

    @Override
    public Optional<T> locate(String key) {
        Optional<T> node = target.locate(key);
        node.ifPresent(n -> {
            int cnt = stats.get(n);
            stats.put(n, cnt + 1);
        });
        return node;
    }

    @Override
    public Set<T> locate(String key, int count) {
        Set<T> nodes = target.locate(key, count);
        nodes.forEach(n -> {
            int cnt = stats.get(n);
            stats.put(n, cnt + 1);
        });
        return nodes;
    }

    @Override
    public int size() {
        return target.size();
    }

    public Map<T, Integer> getStats() {
        return new HashMap<>(stats);
    }

    public void printLoadDistribution() {
        System.out.println();
        System.out.println("################### LOAD DISTRIBUTION ###################");
        System.out.println("Name: " + target.getName());
        System.out.println("Consistent hash: " + target.getClass().getName());
        System.out.println("Hasher: " + target.getHasher());
        System.out.println("Partition Rate: " + target.getPartitionRate());
        System.out.println("Size: " + target.size());
        System.out.println("____________________________________________________");
        for (T node : stats.keySet()) {
            System.out.println("Node [" + node + "] received: " + stats.get(node) + " hits");
        }
        System.out.println();
    }
}
