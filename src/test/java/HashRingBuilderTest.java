import hash.DefaultHasher;
import node.SimpleNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class HashRingBuilderTest {

    @Test
    @DisplayName("Build hash ring and check if all properties were properly set")
    public void build_allPropertiesSet_fullyInitializedHashRing() {
        HashRing<SimpleNode> ring = HashRing.<SimpleNode>newBuilder()
                .name("test_ring")
                .hasher(DefaultHasher.CITY_HASH)
                .partitionRate(3000)
                .nodes(Collections.singleton(SimpleNode.of("key")))
                .build();

        assertEquals(ring.getName(), "test_ring");
        assertEquals(ring.getHasher(), DefaultHasher.CITY_HASH);
        assertEquals(ring.getPartitionRate(), 3000);
        assertIterableEquals(ring.getNodes(), Collections.singleton(SimpleNode.of("key")));
    }

    @Test
    @DisplayName("Build hash ring without any parameters and check if all properties have default values")
    public void build_nonePropertiesSet_hashRingWithDefaultProperties() {
        HashRing<SimpleNode> ring = HashRing.<SimpleNode>newBuilder().build();

        assertNotNull(ring.getName());
        assertFalse(ring.getName().isEmpty());
        assertNotNull(ring.getHasher());
        assertTrue(ring.getPartitionRate() > 0);
        assertTrue(ring.getNodes().isEmpty());
    }

    @Test
    @DisplayName("Build hash ring with negative partitionRate, expected exception")
    public void build_negativePartitionRate_exceptionThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HashRing.<SimpleNode>newBuilder().partitionRate(-1).build()
        );
    }
}
