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

package org.ishugaliy.allgood.consistent.hash;

import org.ishugaliy.allgood.consistent.hash.hasher.DefaultHasher;
import org.ishugaliy.allgood.consistent.hash.hasher.Hasher;
import org.ishugaliy.allgood.consistent.hash.node.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * Implementation of builder pattern for {@link HashRing}
 *
 * @param <T> the type of node to be used in {@link HashRing}
 *
 * @author Iurii Shugalii
 */
public final class HashRingBuilder<T extends Node> {

    private String name;
    private Hasher hash;
    private int partitionRate = 1000;
    private Collection<T> nodes = Collections.emptyList();

    /**
     * Set name of the hash ring
     *
     * @param name the name, default value will be generated
     * @return builder instance
     * @throws NullPointerException if name is null
     */
    public HashRingBuilder<T> name(String name) {
        Objects.requireNonNull(name, "Name can not be null");
        this.name = name;
        return this;
    }

    /**
     * Set hash function implementation.
     *
     * @param hash the hash function, default value is {@link DefaultHasher#MURMUR_3}
     * @return builder instance
     */
    public HashRingBuilder<T> hasher(Hasher hash) {
        this.hash = hash;
        return this;
    }

    /**
     * Set amount of {@link org.ishugaliy.allgood.consistent.hash.partition.Partition} to be create per {@link Node}
     *
     * @param partitionRate the partition rate. Default value is 1000, minimum is 1
     * @return builder instance
     * @throws IllegalArgumentException if partition rate less than 1
     */
    public HashRingBuilder<T> partitionRate(int partitionRate) {
        if (partitionRate < 1) {
            throw new IllegalArgumentException("Replication Factor can not be less than 1");
        }
        this.partitionRate = partitionRate;
        return this;
    }

    /**
     * Initialized hash ring with list of nodes
     *
     * @param nodes the nodes list to be added to hash ring
     * @return builder instance
     * @throws NullPointerException if nodes list is null
     */
    public HashRingBuilder<T> nodes(Collection<T> nodes) {
        Objects.requireNonNull(nodes, "Nodes list can not be null");
        this.nodes = nodes;
        return this;
    }

    /**
     * Build hash ring.
     *
     * @return the hash ring instance
     */
    public HashRing<T> build() {
        name = name != null ? name : generateName();
        hash = hash != null ? hash : DefaultHasher.MURMUR_3;

        HashRing<T> ring = new HashRing<>(name, hash, partitionRate);
        ring.addAll(nodes);
        return ring;
    }

    private String generateName() {
        return "hash_ring_" + new Random().nextInt(10_000);
    }
}
