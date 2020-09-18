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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ServerNodeTest {

    @Test
    @DisplayName("Create node via constructor, check all properties were set")
    public void constructor_allProperties_instance() {
        ServerNode node = new ServerNode("dc1", "192.168.0.1", 80);

        assertEquals(node.getDc(), "dc1");
        assertEquals(node.getIp(), "192.168.0.1");
        assertEquals(node.getPort(), 80);
    }

    @Test
    @DisplayName("Create node via constructor with null dc and ip, check properties were properly")
    public void constructor_dcAndIpIsNull_instance() {
        ServerNode node = new ServerNode(null, null, 80);

        assertEquals(node.getDc(), "");
        assertEquals(node.getIp(), "");
        assertEquals(node.getPort(), 80);
    }

    @TestFactory
    public Stream<DynamicTest> getKey() {
        Node n1 = new ServerNode("dc1", "192.168.0.1", 80);
        Node n2 = new ServerNode("localhost", 80);
        Node n3 = new ServerNode(null, null, 80);
        return Stream.of(
                DynamicTest.dynamicTest(
                        "All properties set",
                        () -> assertEquals("dc1:192.168.0.1:80", n1.getKey())),

                DynamicTest.dynamicTest(
                        "dc is null",
                        () -> assertEquals(":localhost:80", n2.getKey())),

                DynamicTest.dynamicTest(
                        "Empty dc and ip is null",
                        () -> assertEquals("::80", n3.getKey()))
        );
    }

    @TestFactory
    public Stream<DynamicTest> equalsAndHashCode() {
        Node n1 = new ServerNode("dc1", "localhost", 80);
        Node n2 = new ServerNode("dc1", "localhost", 80);
        Node n3 = new ServerNode("dc1", "localhost", 81);
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
