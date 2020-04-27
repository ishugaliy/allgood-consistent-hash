package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.SimpleNode;
import org.ishugaliy.consistent.hash.samples.metrics.HashRingMetrics;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PartitionLoadDistributionSandbox {

    private static final int NODES_COUNT = 3;
    private static final int REQUESTS_COUNT = 100_000;

    public static void main(String[] args) {
        // Build test nodes
        Set<SimpleNode> nodes = buildNodes();

        // Build hash ring with custom partition rate and wrap with HashRingMetrics decorator
        HashRingMetrics<SimpleNode> ring_100000 = buildRing(100_000);
        HashRingMetrics<SimpleNode> ring_10000 = buildRing(10_000);
        HashRingMetrics<SimpleNode> ring_1000 = buildRing(1_000);
        HashRingMetrics<SimpleNode> ring_100 = buildRing(100);
        HashRingMetrics<SimpleNode> ring_10 = buildRing(10);

        // Add nodes to the rings
        ring_100000.addAll(nodes);
        ring_10000.addAll(nodes);
        ring_1000.addAll(nodes);
        ring_100.addAll(nodes);
        ring_10.addAll(nodes);

        // Locate node for each ring
        for (int i = 0; i < REQUESTS_COUNT; i++) {
            String key = UUID.randomUUID().toString();
            ring_100000.locate(key);
            ring_10000.locate(key);
            ring_1000.locate(key);
            ring_100.locate(key);
            ring_10.locate(key);
        }

        // Print load distribution stats
        ring_100000.printLoadDistribution();
        ring_10000.printLoadDistribution();
        ring_1000.printLoadDistribution();
        ring_100.printLoadDistribution();
        ring_10.printLoadDistribution();
    }

    // Build hash ring with specific partition rate
    private static HashRingMetrics<SimpleNode> buildRing(int partitionRate) {
        return new HashRingMetrics<>(HashRing.<SimpleNode>newBuilder()
                .name(partitionRate + "_partition_ring")
                .partitionRate(partitionRate)
                .build());
    }

    private static Set<SimpleNode> buildNodes() {
        return IntStream.range(0, NODES_COUNT)
                .mapToObj(i -> SimpleNode.of("192.168.1." + i))
                .collect(Collectors.toSet());
    }
}
