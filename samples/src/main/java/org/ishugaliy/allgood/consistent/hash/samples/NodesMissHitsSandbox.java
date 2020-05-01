/*
 * The MIT License
 *
 * Copyright (c) 2020 Iurii Shugalii
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

import org.ishugaliy.allgood.consistent.hash.samples.metrics.HashRingMetrics;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NodesMissHitsSandbox {

    private static final int NODES_COUNT = 100;
    private static final int NODES_TO_REMOVE = 7;
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
        ring.printMissHits(REQUESTS_COUNT);
        ring.printLoadDistribution();
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
