package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.ConsistentHash;
import org.ishugaliy.consistent.hash.HashRing;
import org.ishugaliy.consistent.hash.hasher.Hasher;
import org.ishugaliy.consistent.hash.node.SimpleNode;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

public class CustomHasher {

    public static void main(String[] args) {
        ConsistentHash<SimpleNode> ring = HashRing.<SimpleNode>newBuilder()
                .name("custom_hasher_hash_ring")
                .hasher(new MD5Hasher())             // set instance of custom hasher
                .build();

        // Add nodes to the ring
        ring.add(SimpleNode.of("dc1.api-node.1"));
        ring.add(SimpleNode.of("dc2.api-node.2"));
        ring.add(SimpleNode.of("dc3.api-node.3"));

        // Locate node by UUID key and send request
        for (int i = 1; i <= 100; i++) {
            String key = UUID.randomUUID().toString();
            Optional<SimpleNode> node = ring.locate(key);
            node.ifPresent(n -> System.out.println("Send request to node: [" + n + "]"));
        }
    }

    private static class MD5Hasher implements Hasher {

        @Override
        public long hash(String key, int seed) {
            MessageDigest digest = buildDigest();
            byte[] data = digest.digest((key + seed).getBytes());
            return ByteBuffer.wrap(data).getLong();
        }

        private MessageDigest buildDigest() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to build MD5 hash", e);
            }
        }
    }
}
