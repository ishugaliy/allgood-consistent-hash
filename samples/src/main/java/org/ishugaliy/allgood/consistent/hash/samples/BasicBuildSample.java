package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.Optional;
import java.util.UUID;

/**
 * Sample showing default build and usage of {@link HashRing}.
 * <p>
 *
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder} with default settings
 * 2. Add {@link SimpleNode} to the ring
 * 3. Simulate requests sending by locating node with {@link HashRing#locate(String)}
 * 4. Print on which nodes requests were sent
 *
 * @author Iurii Shugalii
 */
public class BasicBuildSample {

    public static void main(String[] args) {
        // Create hash ring with default properties
        ConsistentHash<SimpleNode> ring = HashRing.<SimpleNode>newBuilder().build();

        // Add nodes to the ring
        ring.add(SimpleNode.of("dc1.node.1"));
        ring.add(SimpleNode.of("dc2.node.2"));
        ring.add(SimpleNode.of("dc3.node.3"));

        // Locate node by UUID key and send request
        for (int i = 1; i <= 100; i++) {
            String key = UUID.randomUUID().toString();
            Optional<SimpleNode> node = ring.locate(key);
            node.ifPresent(n -> System.out.println("Send request to node: [" + n + "]"));
        }
    }
}
