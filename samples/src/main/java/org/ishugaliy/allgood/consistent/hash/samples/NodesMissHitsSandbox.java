package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.samples.metrics.HashRingMetrics;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sandbox allows checking consistent hash nodes miss-hits.
 * Showing dependency between miss-hits and partition rate.
 * <p>
 *
 * Case:
 * 1. Build Hash Ring with {@link NodesMissHitsSandbox#PARTITION_RATE} partition rate
 * 2. Add {@link NodesMissHitsSandbox#NODES_COUNT} nodes to the ring
 * 3. Send {@link NodesMissHitsSandbox#REQUESTS_COUNT} requests by locating node with
 *    {@link HashRing#locate(String)} and gather nodes hits statistics.
 * 4. Randomly remove {@link NodesMissHitsSandbox#NODES_TO_REMOVE} nodes from ring
 * 5. Repeat sending request from #3
 * 6. Print node miss and load distribution reports
 *
 * @author Iurii Shugalii
 */
public class NodesMissHitsSandbox {

    private static final int NODES_COUNT = 82;
    private static final int NODES_TO_REMOVE = 4;
    private static final int PARTITION_RATE = 1000;
    private static final int REQUESTS_COUNT = 100_000;

    public static void main(String[] args) {
        HashRingMetrics<SimpleNode> ring = buildRing();

        // Locate node to init nodes hits counters
        for (int i = 0; i < REQUESTS_COUNT; i++) {
            ring.locate(generateKey(i));
        }

        // remove some nodes from the ring
        Random rand = new Random();
        for (int i = 0; i < NODES_TO_REMOVE; i++) {
            List<SimpleNode> nodes = new ArrayList<>(ring.getNodes());
            ring.remove(nodes.get(rand.nextInt(nodes.size() - 1)));
        }

        // Locate node again to calculate miss hits after nodes removal
        for (int i = 0; i < REQUESTS_COUNT; i++) {
            ring.locate(generateKey(i));
        }

        // Print stats, see 'Nodes miss hits' parameter
        ring.printLoadDistribution();
        ring.printMissHits(REQUESTS_COUNT);
    }

    private static HashRingMetrics<SimpleNode> buildRing() {
        return new HashRingMetrics<>(HashRing.<SimpleNode>newBuilder()
                .partitionRate(PARTITION_RATE)
                .nodes(buildNodes())
                .build()
        );
    }

    private static Set<SimpleNode> buildNodes() {
        return IntStream.range(0, NODES_COUNT)
                .mapToObj(i -> SimpleNode.of("192.168.1." + i))
                .collect(Collectors.toSet());
    }

    private static String generateKey(int idx) {
        return "key_" + idx + "_POW_" + Math.pow(idx, 3);
    }
}
