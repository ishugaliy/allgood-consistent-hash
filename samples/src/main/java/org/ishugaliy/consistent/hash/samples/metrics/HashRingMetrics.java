package org.ishugaliy.consistent.hash.samples.metrics;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.Node;

import java.util.*;

import static java.lang.String.*;

public class HashRingMetrics<T extends Node> implements ConsistentHash<T> {

    private final HashRing<T> target;
    private final Map<String, Set<T>> keys = new HashMap<>();
    private final Map<T, Integer> loads = new HashMap<>();

    private int missHits;

    public HashRingMetrics(HashRing<T> ring) {
        this.target = ring;
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public boolean add(T node) {
        loads.putIfAbsent(node, 0);
        return target.add(node);
    }

    @Override
    public boolean addAll(Collection<T> nodes) {
        nodes.forEach(n -> loads.putIfAbsent(n, 0));
        return target.addAll(nodes);
    }

    @Override
    public boolean contains(T node) {
        return target.contains(node);
    }

    @Override
    public boolean remove(T node) {
        loads.remove(node);
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
            int cnt = loads.getOrDefault(n, 0);
            loads.put(n, cnt + 1);
            Set<T> prev = keys.computeIfAbsent(key, k -> new HashSet<>());
            if (!prev.isEmpty() && !prev.contains(n)) {
                missHits++;
            }
            prev.add(n);
        });
        return node;
    }

    @Override
    public Set<T> locate(String key, int count) {
        Set<T> nodes = target.locate(key, count);
        nodes.forEach(n -> {
            int cnt = loads.get(n);
            loads.put(n, cnt + 1);
            Set<T> prev = keys.computeIfAbsent(key, k -> new HashSet<>());
            if (!prev.isEmpty() && !prev.contains(n)) {
                missHits++;
            }
            prev.add(n);
        });
        return nodes;
    }

    @Override
    public int size() {
        return target.size();
    }

    public Map<T, Integer> getLoads() {
        return new HashMap<>(loads);
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
        for (T node : loads.keySet()) {
            System.out.println("Node [" + node + "] received: " + loads.get(node) + " hits");
        }
        System.out.println("____________________________________________________");
        printExtrema();
        printStandardDeviation();
        System.out.println("____________________________________________________");
        System.out.println();
    }

    public void printMissHits(int reqCount) {
        System.out.println();
        System.out.println("########## MISS HITS ############");
        float percent = Math.round(missHits / (float) reqCount * 100);
        System.out.println("Nodes miss: [" + missHits + "] hits. - " + percent + "%");
        System.out.println();
    }

    public void printExtrema() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Integer cnt : loads.values()) {
            if (cnt < min) min = cnt;
            if (cnt > max) max = cnt;
        }
        System.out.println(format("min: [%d] max: [%d], delta: [%d]", min, max, max - min));
        System.out.println("---");
    }

    public void printStandardDeviation() {
        double avg = calculateArithmeticMean();
        double dispersion = calculateDispersion(avg);
        double standDev = Math.round(Math.sqrt(dispersion));
        double standDevPercent = Math.round(standDev / avg * 100);
        System.out.println("arithmetic mean: [" + avg + "] hits");
        System.out.println("stan. deviation: [" + standDev + "] hits - " + standDevPercent + "%");
    }

    public double calculateDispersion(double avg) {
        double deviation = 0;
        for (Integer cnt : loads.values()) {
            deviation += Math.pow(avg - cnt, 2);
        }
        return Math.round(deviation / target.size());
    }

    private double calculateArithmeticMean() {
        float sum = 0;
        for (Integer cnt : loads.values()) {
            sum += cnt;
        }
        return Math.round(sum / target.size());
    }
}
