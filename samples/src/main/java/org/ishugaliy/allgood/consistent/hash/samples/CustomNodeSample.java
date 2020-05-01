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

import org.ishugaliy.allgood.consistent.hash.ConsistentHash;
import org.ishugaliy.allgood.consistent.hash.HashRing;
import org.ishugaliy.allgood.consistent.hash.node.Node;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Sample showing building of {@link HashRing} with custom node.
 * <p>
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder}
 *    with custom {@link CustomNodeSample.MyNode} node
 * 2. Add nodes to the ring
 * 3. Simulate requests sending by locating node with {@link HashRing#locate(String)}
 * 4. Trace to console, on which nodes request was sent
 *
 * @author Iurii Shugalii
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
