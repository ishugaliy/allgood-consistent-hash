package node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ServerNodeTest {

    @TestFactory
    @DisplayName("Create nodes with different value, expected valid keys")
    public Stream<DynamicTest> getKey_key() {
        Node n1 = new ServerNode("dc1", "192.168.0.1", 80);
        DynamicTest test1 = DynamicTest.dynamicTest(
                "All properties set",
                () -> assertEquals("dc1:192.168.0.1:80", n1.getKey()));

        Node n2 = new ServerNode("localhost", 80);
        DynamicTest test2 = DynamicTest.dynamicTest(
                "dc is null",
                () -> assertEquals(":localhost:80", n2.getKey()));

        Node n3 = new ServerNode(null,null, 80);
        DynamicTest test3 = DynamicTest.dynamicTest(
                "Empty dc and ip is null",
                () -> assertEquals("::80", n3.getKey()));

        return Stream.of(test1, test2, test3);
    }

    @Test
    @DisplayName("Test equals and hashcode on same objects, expected true")
    public void equalsAndHashCode_sameProperties_true() {
        Node n1 = new ServerNode("dc1", "localhost", 80);
        Node n2 = new ServerNode("dc1", "localhost", 80);

        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    @DisplayName("Test equals and hashcode test on different objects, expected false")
    public void equalsAndHashCode_differentPorts_false() {
        Node n1 = new ServerNode("dc1", "localhost", 80);
        Node n2 = new ServerNode("dc1", "localhost", 81);

        assertNotEquals(n1, n2);
        assertNotEquals(n1.hashCode(), n2.hashCode());
    }
}
