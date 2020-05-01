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
import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.node.ServerNode;

import java.util.Arrays;
import java.util.Set;


/**
 * Sample showing advanced building and usage of {@link HashRing}.
 * <p>
 * Case:
 * 1. Create hash ring via {@link org.ishugaliy.allgood.consistent.hash.HashRingBuilder} with custom properties
 * 2. Add {@link ServerNode} nodes to the ring
 * 3. Simulate requests sending by locating two node with {@link HashRing#locate(String, int)}
 * 4. Remove some nodes from the ring
 * 5. Repeat sending of requests from #2
 * 6. Trace to console, on which nodes requests were sent
 *
 * @author Iurii Shugalii
 */
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
                .hasher(DefaultHasher.METRO_HASH)   // hash function to distribute partitions
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