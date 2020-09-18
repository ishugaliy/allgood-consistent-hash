/*
 * The MIT License
 *
 * Copyright (c) 2020 Yuriy Shugaliy
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

package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.hasher.Hasher;
import org.ishugaliy.allgood.consistent.hash.node.Node;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sandbox allows checking consistent hash performance.
 * Showing how execution time depends on the ring size and partition rate.
 * <p>
 *
 * Case:
 * 1. Build hash rings with
 *    {@link WorkloadBenchmarkSandbox#PARTITION_RATE}
 *    {@link WorkloadBenchmarkSandbox#NODES_COUNT}
 *    {@link WorkloadBenchmarkSandbox#HASHER}
 * 2. Benchmark {@link HashRing#add(Node)} api
 * 3. Benchmark {@link HashRing#locate(String)} api
 *    with {@link WorkloadBenchmarkSandbox#REQUEST_COUNTS}
 * 4. Benchmark {@link HashRing#locate(String, int)} api
 *    with {@link WorkloadBenchmarkSandbox#REQUEST_COUNTS}
 *
 * @author Yuriy Shugaliy
 */

// TODO: use JMH benchmark instead - https://openjdk.java.net/projects/code-tools/jmh/
public class WorkloadBenchmarkSandbox {

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
