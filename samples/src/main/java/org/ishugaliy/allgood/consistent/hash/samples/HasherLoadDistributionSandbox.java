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

import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.samples.metrics.HashRingMetrics;
import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class HasherLoadDistributionSandbox {

    private static final int NODES_COUNT = 10;
    private static final int REQUESTS_COUNT = 100_000;
    private static final int PARTITION_RATE = 1000;

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
