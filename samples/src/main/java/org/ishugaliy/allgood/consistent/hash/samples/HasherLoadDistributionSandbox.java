package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;
import org.ishugaliy.allgood.consistent.hash.samples.metrics.HashRingMetrics;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sandbox allows checking consistent hash load distribution between nodes with different hash functions.
 * Showing how distribution depends on the hash function.
 * <p>
 *
 * Case:
 * 1. Build Hash Ring for every existing {@link DefaultHasher}
 *    with {@link HasherLoadDistributionSandbox#PARTITION_RATE}
 * 2. Add {@link HasherLoadDistributionSandbox#NODES_COUNT} nodes to each ring
 * 3. Send {@link HasherLoadDistributionSandbox#REQUESTS_COUNT} requests to each ring
 *    by locating node with {@link HashRing#locate(String)} and gather nodes hits statistics.
 * 4. Randomly remove {@link HasherLoadDistributionSandbox#NODES_TO_REMOVE} nodes from each ring
 * 5. Repeat sending request from #3
 * 6. Print load distribution reports
 *
 * @author Yuriy Shugaliy
 */

@SuppressWarnings("all")
public class HasherLoadDistributionSandbox {

    private static final int NODES_COUNT = 10;
    private static final int REQUESTS_COUNT = 100_000;
    private static final int PARTITION_RATE = 10;

    public static void main(String[] args) {
        // Create test nodes
        Set<SimpleNode> nodes = buildNodes();

        // Create hash ring for every default hasher
        List<ConsistentHash<SimpleNode>> rings = Arrays.stream(DefaultHasher.values())
                .map(HasherLoadDistributionSandbox::buildRing)
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
