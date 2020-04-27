package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.SimpleNode;
import org.ishugaliy.consistent.hash.samples.metrics.HashRingMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NodesMissHitsSandbox {

    private static final int NODES_COUNT = 10;
    private static final int NODES_TO_REMOVE = 3;
    private static final int PARTITION_KEY = 1000;
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
        ring.printMissHits();
        ring.printLoadDistribution();
    }

    private static HashRingMetrics<SimpleNode> buildRing() {
        return new HashRingMetrics<>(HashRing.<SimpleNode>newBuilder()
                .partitionRate(PARTITION_KEY)
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
