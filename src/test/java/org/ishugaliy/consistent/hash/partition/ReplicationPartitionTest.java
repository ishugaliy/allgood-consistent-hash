package org.ishugaliy.consistent.hash.partition;

import org.ishugaliy.consistent.hash.node.Node;
import org.ishugaliy.consistent.hash.node.SimpleNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ExtendWith(MockitoExtension.class)
public class ReplicationPartitionTest {

    @Test
    @DisplayName("Create partition via constructor, check if all properties were set")
    public void constructor_allProperties(@Mock Node node) {
        ReplicationPartition<Node> part =
                new ReplicationPartition<>(0, node, 301);

        assertEquals(0, part.getIndex());
        assertEquals(node, part.getNode());
        assertEquals(301, part.getSlot());
    }

    @Test
    @DisplayName("Create partition with null node, expected NPE")
    public void constructor_nodeIsNull_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> new ReplicationPartition<>(-1, null));
    }

    @TestFactory
    public Stream<DynamicTest> getPartitionKey() {
        Partition<Node> p1 = new ReplicationPartition<>(0, SimpleNode.of("dc1.labs.123"));
        Partition<Node> p2 = new ReplicationPartition<>(12412, SimpleNode.of("ex ample    "));
        Partition<Node> p3 = new ReplicationPartition<>(-1, SimpleNode.of(""));
        return Stream.of(
                dynamicTest(
                        "Node key with server host",
                        () -> assertEquals("rp0:dc1.labs.123", p1.getPartitionKey())),

                dynamicTest(
                        "Node key with spaces",
                        () -> assertEquals("rp12412:ex ample    ", p2.getPartitionKey())),

                dynamicTest(
                        "Node key is empty",
                        () -> assertEquals("rp-1:", p3.getPartitionKey()))
        );
    }

    @TestFactory
    public Stream<DynamicTest> equalsAndHashCode(@Mock Node node) {
        Partition<Node> p1 = new ReplicationPartition<>(1, node, 1);
        Partition<Node> p2 = new ReplicationPartition<>(1, node, 1);
        Partition<Node> p3 = new ReplicationPartition<>(1, SimpleNode.of("2"), 1);
        return Stream.of(
                dynamicTest("Partitions with equals properties, expected true",
                        () -> {
                            assertEquals(p1, p2);
                            assertEquals(p1.hashCode(), p2.hashCode());
                        }),
                dynamicTest("Different partitions, expected false",
                        () -> {
                            assertNotEquals(p1, p3);
                            assertNotEquals(p1.hashCode(), p3.hashCode());
                        })
        );
    }
}
