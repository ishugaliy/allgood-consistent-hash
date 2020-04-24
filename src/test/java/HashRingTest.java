import hash.Hasher;
import node.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.stream.Stream;

import static hash.DefaultHasher.METRO_HASH;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashRingTest {

    @Test
    @DisplayName("Create ring via constructor, check all properties were set")
    public void constructor_allProperties_instance() {
        HashRing<Node> ring = new HashRing<>("ring", METRO_HASH, 1);

        assertEquals("ring", ring.getName());
        assertEquals(METRO_HASH, ring.getHasher());
        assertEquals(1, ring.getPartitionRate());
    }

    @TestFactory
    public Stream<DynamicTest> add(@Mock(answer = Answers.RETURNS_SMART_NULLS) Node n1,
                                   @Mock Node n2) {
        HashRing<Node> ring = HashRing.newBuilder().build();
        return Stream.of(
                dynamicTest("Add node to empty ring, expected true",
                        () -> {
                            assertTrue(ring.add(n1));
                            verify(n1, atLeastOnce()).getKey();
                        }),

                dynamicTest("Add already existing node to the ring, expected false",
                        () -> assertFalse(ring.add(n1))),

                dynamicTest("Add node with null key, expected true",
                        () -> assertTrue(ring.add(n2))),

                dynamicTest("Add null node, expected false",
                        () -> assertFalse(ring.add(null)))
        );
    }

    @Test
    @DisplayName("Create ring with custom partition rate, check if all partitioned were created")
    public void add_customPartitionRate_true(@Mock(answer = Answers.RETURNS_SMART_NULLS) Node node) {
        HashRing<Node> ring = HashRing.newBuilder().partitionRate(1000).build();

        assertTrue(ring.add(node));
        assertTrue(ring.contains(node));
        verify(node, times(1000)).getKey();
    }

    @Test
    @DisplayName("Simulate slot collision in the ring and check that partition key was rehashed with new seed")
    public void add_collisionInHasher_true(@Mock Node n1, @Mock Node n2) {
        Hasher hasher = mock(Hasher.class, in -> in.getArgument(1, Integer.class).longValue());
        when(n1.getKey()).thenReturn("key1");
        when(n2.getKey()).thenReturn("key2");
        HashRing<Node> ring = HashRing.newBuilder()
                .nodes(singleton(n1))
                .hasher(hasher)
                .partitionRate(1)
                .build();

        boolean added = ring.add(n2);

        assertTrue(added);
        assertTrue(ring.contains(n1));
        assertTrue(ring.contains(n2));
        verify(n1, only()).getKey();
        verify(n2, only()).getKey();
        verify(hasher, times(1)).hash("rp0:key1", 0);
        verify(hasher, times(1)).hash("rp0:key2", 0);
        verify(hasher, times(1)).hash("rp0:key2", 1);
    }

    @TestFactory
    public Stream<DynamicTest> addAll(@Mock Node n1, @Mock Node n2, @Mock Node n3) {
        HashRing<Node> ring = HashRing.newBuilder().build();
        return Stream.of(
                dynamicTest("Add nodes to empty ring, expected true",
                        () -> {
                            assertTrue(ring.addAll(asList(n1, n2)));
                            assertEquals(2, ring.size());
                        }),
                dynamicTest("Add nodes to the ring, that are already there, expected false",
                        () -> {
                            assertFalse(ring.addAll(asList(n1, n2)));
                            assertEquals(2, ring.size());
                        }),
                dynamicTest("Add nodes list with null value, expected adding of nonnull node",
                        () -> {
                            assertTrue(ring.addAll(asList(n3, null)));
                            assertEquals(3, ring.size());
                        }),
                dynamicTest("Add nodes list with null value and already added nodes, expected false",
                        () -> {
                            assertFalse(ring.addAll(asList(n1, n3, null)));
                            assertEquals(3, ring.size());
                        }),
                dynamicTest("Add empty node list, expected false",
                        () -> {
                            assertFalse(ring.addAll(new ArrayList<>()));
                            assertEquals(3, ring.size());
                        }),
                dynamicTest("Add null node list, expected false",
                        () -> {
                            assertFalse(ring.addAll(null));
                            assertEquals(3, ring.size());
                        })
        );
    }

    @Test
    @DisplayName("Add and remove node from the ring, assert if ring contains it before and after removing")
    public void contains_nodeInTheRing_true(@Mock Node node) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(singleton(node)).build();
        assertTrue(ring.contains(node));
        ring.remove(node);
        assertFalse(ring.contains(node));
    }

    @Test
    @DisplayName("Remove node, that was added before, two times and check removing status")
    public void remove_nodeInTheRing_removeNode(@Mock Node node) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(singleton(node)).build();
        assertTrue(ring.remove(node));
        assertFalse(ring.remove(node));
    }

    @Test
    @DisplayName("Get nodes from ring with nodes, expected nodes list")
    public void getNodes_ringHasNodes_nodesList(@Mock Node n1, @Mock Node n2) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(asList(n1, n2)).build();
        assertTrue(ring.getNodes().containsAll(asList(n1, n2)));
    }

    @Test
    @DisplayName("Get nodes from empty ring, expected empty list")
    public void getNodes_emptyRing_nodesList() {
        HashRing<Node> ring = HashRing.newBuilder().build();
        assertTrue(ring.getNodes().isEmpty());
    }

    @Test
    @DisplayName("Add and remove node to the ring, and check size")
    public void size_ringHasNode(@Mock Node node) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(singleton(node)).build();
        assertEquals(1, ring.size());
        ring.remove(node);
        assertEquals(0, ring.size());
    }

    @TestFactory
    public Stream<DynamicTest> locate(@Mock Node n1, @Mock Node n2, @Mock Node n3) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(asList(n1, n2, n3)).build();
        HashRing<Node> emptyRing = HashRing.newBuilder().build();
        return Stream.of(
                dynamicTest("Locate node, expected node",
                        () -> assertTrue(ring.locate("key").isPresent())),

                dynamicTest("Locate node in empty ring, expected empty",
                        () -> assertFalse(emptyRing.locate("key").isPresent())),

                dynamicTest("Locate node with null key, expected empty",
                        () -> assertFalse(ring.locate(null).isPresent()))
        );
    }

    @TestFactory
    public Stream<DynamicTest> locate_countNodes(@Mock Node n1, @Mock Node n2, @Mock Node n3) {
        HashRing<Node> ring = HashRing.newBuilder().nodes(asList(n1, n2, n3)).build();
        HashRing<Node> emptyRing = HashRing.newBuilder().build();
        return Stream.of(
                dynamicTest("Locate 3 nodes, expected 3 nodes",
                        () -> assertEquals(3, ring.locate("key", 3).size())),

                dynamicTest("Locate 2 nodes, expected 2 nodes",
                        () -> assertEquals(2, ring.locate("key", 2).size())),

                dynamicTest("Locate 5 nodes, expected 3 nodes",
                        () -> assertEquals(3, ring.locate("key", 5).size())),

                dynamicTest("Locate with null key, expected empty list",
                        () -> assertTrue(ring.locate(null, 3).isEmpty())),

                dynamicTest("Locate with negative count, expected empty list",
                        () -> assertTrue(ring.locate("key", -1).isEmpty())),

                dynamicTest("Locate 2 nodes in empty ring, expected empty list",
                        () -> assertTrue(emptyRing.locate("key", 2).isEmpty()))
        );
    }
}
