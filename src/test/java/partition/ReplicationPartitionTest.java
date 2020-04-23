package partition;

import node.Node;
import node.SimpleNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ReplicationPartitionTest {

    @Test
    @DisplayName("Create partition with null node, expected NPE")
    public void constructor_nodeIsNull_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> new ReplicationPartition<>(-1, null));
    }

    @TestFactory
    @DisplayName("Generate partitions for nodes with different keys, expected valid partition keys")
    public Stream<DynamicTest> getPartitionKey() {
        Partition<Node> p1 = new ReplicationPartition<>(0, SimpleNode.of("dc1.labs.123"));
        DynamicTest p1Test = DynamicTest.dynamicTest(
                "Node key with server host",
                () -> assertEquals("rp0:dc1.labs.123", p1.getPartitionKey()));

        Partition<Node> p2 = new ReplicationPartition<>(12412, SimpleNode.of("ex ample    "));
        DynamicTest p2Test = DynamicTest.dynamicTest(
                "Node key with spaces",
                () -> assertEquals("rp12412:ex ample    ", p2.getPartitionKey()));

        Partition<Node> p3 = new ReplicationPartition<>(-1, SimpleNode.of(""));
        DynamicTest p3Test = DynamicTest.dynamicTest(
                "Node key is empty",
                () -> assertEquals("rp-1:", p3.getPartitionKey()));

        return Stream.of(p1Test, p2Test, p3Test);
    }

    @Test
    @DisplayName("Test equals and hashcode on same objects, expected true")
    public void equalsAndHashCode_sameProperties_true() {
        SimpleNode node = SimpleNode.of("key");
        Partition<Node> p1 = new ReplicationPartition<>(1, node, 1);
        Partition<Node> p2 = new ReplicationPartition<>(1, node, 1);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("Test equals and hashcode test on different objects, expected false")
    public void equalsAndHashCode_differentNodes_false() {
        Partition<Node> p1 = new ReplicationPartition<>(1, SimpleNode.of("key"), 1);
        Partition<Node> p2 = new ReplicationPartition<>(1, SimpleNode.of("2"), 1);

        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }
}
