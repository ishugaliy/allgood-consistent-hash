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

package org.ishugaliy.allgood.consistent.hash.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SimpleNodeTest {

    @Test
    @DisplayName("Create node with null value, expected NPE")
    public void of() {
        SimpleNode node = SimpleNode.of("v");
        assertEquals("v", node.getKey());
    }

    @Test
    @DisplayName("Create node with null value, expected NPE")
    public void of_valueIsNull_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> SimpleNode.of(null));
    }

    @TestFactory
    public Stream<DynamicTest> getKey() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Value not empty",
                        () -> assertEquals("123", SimpleNode.of("123").getKey())),

                DynamicTest.dynamicTest(
                        "Value with spaces",
                        () -> assertEquals(" a", SimpleNode.of(" a").getKey())),

                DynamicTest.dynamicTest(
                        "Value is empty",
                        () -> assertEquals("", SimpleNode.of("").getKey()))
        );
    }

    @TestFactory
    public Stream<DynamicTest> equalsAndHashCode() {
        Node n1 = SimpleNode.of("key");
        Node n2 = SimpleNode.of("key");
        Node n3 = SimpleNode.of("123");
        return Stream.of(
                dynamicTest("Nodes with equals properties, expected true",
                        () -> {
                            assertEquals(n1, n2);
                            assertEquals(n1.hashCode(), n2.hashCode());
                        }),
                dynamicTest("Different nodes, expected false",
                        () -> {
                            assertNotEquals(n1, n3);
                            assertNotEquals(n1.hashCode(), n3.hashCode());
                        })
        );
    }
}
