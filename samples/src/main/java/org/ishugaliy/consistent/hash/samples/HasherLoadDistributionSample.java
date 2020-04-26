package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.samples.analysis.HashRingMetrics;
import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.consistent.hash.node.SimpleNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class HasherLoadDistributionSample {

    private static final int NODES_COUNT = 10;
    private static final int REQUESTS_COUNT = 100_000;
    private static final int PARTITION_RATE = 1000;

    public static void main(String[] args) {
        // Create test nodes
        Set<SimpleNode> nodes = buildNodes();

        // Create hash ring for every default hasher
        List<ConsistentHash<SimpleNode>> rings = Arrays.stream(DefaultHasher.values())
                .map(HasherLoadDistributionSample::buildRing)
                .peek(ring -> ring.addAll(nodes))
                .collect(Collectors.toList());

        // Locate node with random UUID key
        for (int i = 0; i < REQUESTS_COUNT; i++) {
            String key = UUID.randomUUID().toString();
            rings.forEach(ring -> ring.locate(key));
        }

        // Print load distribution for all rings
        rings.stream()
                .map(ring -> (HashRingMetrics) ring)
                .forEach(HashRingMetrics::printLoadDistribution);
    }

    private static Set<SimpleNode> buildNodes() {
        return IntStream.range(0, NODES_COUNT)
                .mapToObj(i -> SimpleNode.of("192.168.1." + i))
                .collect(Collectors.toSet());
    }

    // Build Hash ring for provided hasher and wrap it with HashRingMetrics decorator.
    private static ConsistentHash<SimpleNode> buildRing(DefaultHasher hasher) {
        return new HashRingMetrics<>(
                HashRing.<SimpleNode>newBuilder()
                        .name(hasher.name().toLowerCase() + "_ring")
                        .partitionRate(PARTITION_RATE)
                        .hasher(hasher)
                        .build()
        );
    }
}
