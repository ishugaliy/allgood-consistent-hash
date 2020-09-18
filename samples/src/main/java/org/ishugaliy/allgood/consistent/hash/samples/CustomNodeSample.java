package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.node.Node;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Sample showing build of {@link HashRing} with custom implementation of {@link Node}.
 * <p>
 *
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder}
 *    with custom node {@link CustomNodeSample.MyNode}
 * 2. Add nodes to the ring
 * 3. Simulate requests sending by locating node with {@link HashRing#locate(String)}
 * 4. Print on which nodes request was sent
 *
 * @author Yuriy Shugaliy
 */
public class CustomNodeSample {

    public static void main(String[] args) {
        // Create hash ring with custom node
        ConsistentHash<MyNode> ring = HashRing.<MyNode>newBuilder()
                .name("custom_node_hash_ring")
                .build();

        // Add custom nodes
        ring.add(new MyNode(UUID.randomUUID()));
        ring.add(new MyNode(UUID.randomUUID()));
        ring.add(new MyNode(UUID.randomUUID()));

        // Locate node
        for (int i = 0; i < 100; i++) {
            Optional<MyNode> node = ring.locate("key" + i);
            node.ifPresent(n -> System.out.println("Send request to [" + n + "]"));
        }
    }

    private static class MyNode implements Node {

        private final UUID id;

        public MyNode(UUID id) {
            this.id = id;
        }

        @Override
        public String getKey() {
            return id.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MyNode)) return false;

            MyNode myNode = (MyNode) o;

            return id != null ? id.equals(myNode.id) : myNode.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", MyNode.class.getSimpleName() + "[", "]")
                    .add("id=" + id)
                    .toString();
        }
    }
}
