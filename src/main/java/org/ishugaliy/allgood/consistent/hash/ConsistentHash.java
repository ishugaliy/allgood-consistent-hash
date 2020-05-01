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

import org.ishugaliy.allgood.consistent.hash.node.Node;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Base interface for implementation of Consistent Hash
 *
 * @param <T> the type of node.
 *
 * @author Iurii Shugalii
 */
public interface ConsistentHash<T extends Node> {

    /**
     * Return name of the consistent hash instance
     *
     * @return the name
     */
    String getName();

    /**
     * Add node to the consistent hash
     *
     * @param node the node to be added
     * @return <tt>true</tt> if node was added or
     *         <tt>false</tt> if not or node is null.
     */
    boolean add(T node);

    /**
     * Add all provided nodes to the consistent hash
     *
     * @param nodes the nodes to be added
     * @return <tt>true</tt> if at least one of the nodes were added or
     *         <tt>false</tt> if no nodes were added or all of them are null.
     */
    boolean addAll(Collection<T> nodes);

    /**
     * Check if consistent hash contains provided node
     *
     * @param node the node to be checked
     * @return <tt>true</tt> if consistent hash contains node or
     *         <tt>false</tt> if not or node is null
     */
    boolean contains(T node);

    /**
     * Remove node from the consistent hash
     *
     * @param node the node to be removed
     * @return <tt>true</tt> if node was removed or
     *         <tt>false</tt> if not or node is null
     */
    boolean remove(T node);

    /**
     * Return list of all nodes that are inside consistent hash
     *
     * @return the nodes list
     */
    Set<T> getNodes();

    /**
     * Find node which most matches the key
     *
     * @param key the key
     * @return the node or {@link Optional#empty()} if no node was found
     */
    Optional<T> locate(String key);

    /**
     * Find nodes which most matches the key
     *
     * @param key   the key
     * @param count the amount for nodes to be matched
     * @return the nodes list or empty list if no nodes were found
     */
    Set<T> locate(String key, int count);

    /**
     * Return number of nodes inside consistent hash
     *
     * @return the size of consistent hash
     */
    int size();
}
