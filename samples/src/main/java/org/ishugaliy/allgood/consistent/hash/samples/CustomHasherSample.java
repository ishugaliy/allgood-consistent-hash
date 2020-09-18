package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.hasher.Hasher;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;


/**
 * Sample showing build of {@link HashRing} with custom MD5 hash function.
 * <p>
 *
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder}
 *    with custom {@link MD5Hasher} hasher
 * 2. Add {@link SimpleNode} nodes to the ring
 * 3. Simulate requests sending by locating node with {@link HashRing#locate(String)}
 * 4. Print on which nodes request was sent
 *
 * @author Yuriy Shugaliy
 */
public class CustomHasherSample {

    public static void main(String[] args) {
        ConsistentHash<SimpleNode> ring = HashRing.<SimpleNode>newBuilder()
                .name("custom_hasher_hash_ring")
                .hasher(new MD5Hasher())
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
