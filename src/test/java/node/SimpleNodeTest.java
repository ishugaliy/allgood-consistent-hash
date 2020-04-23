package node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleNodeTest {

    @Test
    @DisplayName("Create node with null value, expected NPE")
    public void of_nodeIsNull_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> SimpleNode.of(null));
    }

    @TestFactory
    @DisplayName("Create nodes with different value, expected valid keys")
    public Stream<DynamicTest> getKey_key() {
        DynamicTest test1 = DynamicTest.dynamicTest(
                "Value not empty",
                () -> assertEquals("123", SimpleNode.of("123").getKey()));

        DynamicTest test2 = DynamicTest.dynamicTest(
                "Value with spaces",
                () -> assertEquals(" a", SimpleNode.of(" a").getKey()));

        DynamicTest test3 = DynamicTest.dynamicTest(
                "Value is empty",
                () -> assertEquals("", SimpleNode.of("").getKey()));

        return Stream.of(test1, test2, test3);
    }

    @Test
    @DisplayName("Test equals and hashcode on same objects, expected true")
    public void equalsAndHashCode_sameProperties_true() {
        Node n1 = SimpleNode.of("key");
        Node n2 = SimpleNode.of("key");

        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    @DisplayName("Test equals and hashcode test on different objects, expected false")
    public void equalsAndHashCode_differentValues_false() {
        Node n1 = SimpleNode.of("key");
        Node n2 = SimpleNode.of("123");

        assertNotEquals(n1, n2);
        assertNotEquals(n1.hashCode(), n2.hashCode());
    }
}
