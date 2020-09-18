/*
 * The MIT License
 *
 * Copyright (c) 2020 Yuriy Shugaliy
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

package org.ishugaliy.allgood.consistent.hash;

import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class HashRingBuilderTest {

    @Test
    @DisplayName("Build hash ring and check if all properties were properly set")
    public void build_allPropertiesSet_fullyInitializedHashRing() {
        HashRing<SimpleNode> ring = HashRing.<SimpleNode>newBuilder()
                .name("test_ring")
                .hasher(DefaultHasher.CITY_HASH)
                .partitionRate(3000)
                .nodes(Collections.singleton(SimpleNode.of("key")))
                .build();

        assertEquals(ring.getName(), "test_ring");
        assertEquals(ring.getHasher(), DefaultHasher.CITY_HASH);
        assertEquals(ring.getPartitionRate(), 3000);
        assertIterableEquals(ring.getNodes(), Collections.singleton(SimpleNode.of("key")));
    }

    @Test
    @DisplayName("Build hash ring without any parameters and check if all properties have default values")
    public void build_nonePropertiesSet_hashRingWithDefaultProperties() {
        HashRing<SimpleNode> ring = HashRing.<SimpleNode>newBuilder().build();

        assertNotNull(ring.getName());
        assertFalse(ring.getName().isEmpty());
        assertNotNull(ring.getHasher());
        assertTrue(ring.getPartitionRate() > 0);
        assertTrue(ring.getNodes().isEmpty());
    }

    @Test
    @DisplayName("Build hash ring with negative partitionRate, expected exception")
    public void build_negativePartitionRate_exceptionThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HashRing.<SimpleNode>newBuilder().partitionRate(-1).build()
        );
    }
}
