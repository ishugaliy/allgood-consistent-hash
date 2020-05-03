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

package org.ishugaliy.allgood.consistent.hash.node;

import org.ishugaliy.allgood.consistent.hash.annotation.Generated;

import java.util.Objects;

/**
 * Simple implementation of {@link Node}.
 * Wrap String value and return it as a key.
 *
 * @author Iurii Shugalii
 */
public class SimpleNode implements Node {

    private final String value;

    private SimpleNode(String value) {
        Objects.requireNonNull(value, "Value can not be null");
        this.value = value;
    }

    /**
     * Factory method to create instance of the class.
     *
     * @return the instance of the class
     */
    public static SimpleNode of(String value) {
        return new SimpleNode(value);
    }

    @Override
    public String getKey() {
        return value;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleNode)) return false;
        SimpleNode that = (SimpleNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @Generated
    public String toString() {
        return value;
    }
}
