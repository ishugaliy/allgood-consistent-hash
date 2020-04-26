package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.node.SimpleNode;

import java.util.Optional;
import java.util.UUID;

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
