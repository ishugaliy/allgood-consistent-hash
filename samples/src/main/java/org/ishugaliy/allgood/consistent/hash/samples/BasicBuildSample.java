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
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.Optional;
import java.util.UUID;

/**
 * Sample showing default building and usage of {@link HashRing}.
 * <p>
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder} with default settings
 * 2. Add {@link SimpleNode} to the ring
 * 3. Simulate requests sending by locating node with {@link HashRing#locate(String)}
 * 4. Trace to console, on which nodes requests were sent
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
