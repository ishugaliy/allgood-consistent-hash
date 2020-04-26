package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.SimpleNode;
import org.ishugaliy.consistent.hash.samples.analysis.HashRingMetrics;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PartitionRateLoadDistributionSample {

    private static final int NODES_COUNT = 10;
    private static final int REQUESTS_COUNT = 100_000;

    public static void main(String[] args) {
        // Build test nodes
        Set<SimpleNode> nodes = buildNodes();

        // Build hash ring with custom partition rate and wrap with HashRingMetrics decorator
        HashRingMetrics<SimpleNode> ring10000 = buildRing(100_000);
        HashRingMetrics<SimpleNode> ring1000 = buildRing(1_000);
        HashRingMetrics<SimpleNode> ring100 = buildRing(100);
        HashRingMetrics<SimpleNode> ring10 = buildRing(10);

        // Add nodes to the rings
        ring10000.addAll(nodes);
        ring1000.addAll(nodes);
        ring100.addAll(nodes);
        ring10.addAll(nodes);

        // Locate node for each ring
        for (int i = 0; i < REQUESTS_COUNT; i++) {
            String key = UUID.randomUUID().toString();
            ring10000.locate(key);
            ring1000.locate(key);
            ring100.locate(key);
            ring10.locate(key);
        }

        // Print load distribution stats
        ring10000.printLoadDistribution();
        ring1000.printLoadDistribution();
        ring100.printLoadDistribution();
        ring10.printLoadDistribution();
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
