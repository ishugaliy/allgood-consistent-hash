package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.consistent.hash.hasher.Hasher;
import org.ishugaliy.consistent.hash.node.SimpleNode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: use jmh benchmark instead - https://openjdk.java.net/projects/code-tools/jmh/
public class WorkloadSample {

    private static final Hasher HASHER = DefaultHasher.MURMUR_3;
    private static final int PARTITION_RATE = 1000;
    private static final int NODES_COUNT = 10_000;
    private static final int REQUEST_COUNTS = 1_000_000;

    // nodes count to be located per request
    private static final int LOCATE_NODES_COUNT = 5;

    public static void main(String[] args) {
        // Build hash ring
        ConsistentHash<SimpleNode> ring = HashRing.<SimpleNode>newBuilder()
                .partitionRate(PARTITION_RATE)
                .hasher(HASHER)
                .build();

        System.out.println(ring);
        System.out.println("-------------------------------------");

        benchmarkNodesAdd(ring);
        benchmarkLocate(ring);
        benchmarkLocateN(ring);
    }

    private static void benchmarkNodesAdd(ConsistentHash<SimpleNode> ring) {
        System.out.println("############# NODES ADD ############");

        long start = System.currentTimeMillis();
        long tmp = start;
        List<SimpleNode> nodes = buildNodes();

        for (int i = 0; i < nodes.size(); i++) {
            ring.add(nodes.get(i));
            if (i % 100 == 0) {
                System.out.println(
                        String.format("Total nodes: [%d] - %s added in: %f .sec",
                                i, 100, (float) (System.currentTimeMillis() - tmp) / 100));
                tmp = System.currentTimeMillis();
            }
        }
        System.out.println("---");
        System.out.println(String.format("Total nodes: [%d] - added in: %f .sec", NODES_COUNT,
                (float) (System.currentTimeMillis() - start) / 1000));

        System.out.println();
    }

    private static void benchmarkLocate(ConsistentHash<SimpleNode> ring) {
        System.out.println("############# LOCATE ############");

        long start = System.currentTimeMillis();
        for (int i = 1; i <= REQUEST_COUNTS; i++) {
            String key = UUID.randomUUID().toString();
            ring.locate(key);
        }
        System.out.println(String.format("Total req: [%d] - located in: %f .sec", REQUEST_COUNTS,
                (float) (System.currentTimeMillis() - start) / 1000));
        System.out.println();
    }

    private static void benchmarkLocateN(ConsistentHash<SimpleNode> ring) {
        System.out.println("############# LOCATE N ############");

        long start = System.currentTimeMillis();
        long tmp = start;
        for (int i = 1; i <= REQUEST_COUNTS; i++) {
            String key = UUID.randomUUID().toString();
            ring.locate(key, LOCATE_NODES_COUNT);
            if (i % 10_000 == 0) {
                System.out.println(
                        String.format("Total req: [%d] - %s located in: %f .sec",
                                i, 100_000, (float) (System.currentTimeMillis() - tmp) / 1000));
                tmp = System.currentTimeMillis();
            }
        }
        System.out.println("---");
        System.out.println(String.format("Total req: [%d] - located in: %f .sec", REQUEST_COUNTS,
                (float) (System.currentTimeMillis() - start) / 1000));

        System.out.println();
    }

    private static List<SimpleNode> buildNodes() {
        return IntStream.range(0, NODES_COUNT)
                .mapToObj(idx -> SimpleNode.of("aws.node" + idx + ".api." + Math.round(Math.pow(idx, 2))))
                .collect(Collectors.toList());
    }

}
