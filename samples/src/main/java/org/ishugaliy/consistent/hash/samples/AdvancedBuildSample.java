package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.consistent.hash.node.ServerNode;

import java.util.Arrays;
import java.util.Set;

public class AdvancedBuildSample {

    public static void main(String[] args) {
        // Create nodes
        ServerNode n1 = new ServerNode("192.168.1.1", 80);
        ServerNode n2 = new ServerNode("192.168.1.132", 80);
        ServerNode n3 = new ServerNode("aws", "11.32.98.1", 9231);
        ServerNode n4 = new ServerNode("aws", "11.32.328.1", 9231);

        // Build hash ring
        ConsistentHash<ServerNode> ring = HashRing.<ServerNode>newBuilder()
                .name("file_cache_hash_ring")       // set hash ring name
                .hasher(DefaultHasher.MURMUR_3)     // hash function to distribute partitions
                .partitionRate(10)                  // number of partitions per node
                .nodes(Arrays.asList(n1, n2))       // initial nodes list
                .build();

        // Add n3 and n4 nodes to the ring
        ring.addAll(Arrays.asList(n3, n4));
        sendRequest(ring);

        // Remove n1 from the ring
        ring.remove(n1);
        sendRequest(ring);
    }

    // Locate 2 nodes from the ring and send request them them
    private static void sendRequest(ConsistentHash<ServerNode> ring) {
        for (int i = 1; i <= 10; i++) {
            String key = "key_" + i;
            Set<ServerNode> nodes = ring.locate(key, 2);

            System.out.println("Send " + i + " request to: ");
            nodes.forEach(n -> System.out.println("\t\t" + n));
        }
    }
}