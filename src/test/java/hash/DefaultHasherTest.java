package hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

public class DefaultHasherTest {

    @TestFactory
    @DisplayName("For all default hasher check hash is calculated and not zero")
    public Stream<DynamicTest> calculateHash_nonZeroValue() {
        String key = "Best key123_ever+:.!";
        int seed = 1231238;

        return Arrays
                .stream(DefaultHasher.values())
                .map(hasher -> dynamicTest(
                        hasher.name(),
                        () -> assertTrue(hasher.hash(key, seed) != 0))
                );
    }

    @TestFactory
    @DisplayName("For all default hasher check hash is not equals for two different keys")
    public Stream<DynamicTest> calculateHash_true() {
        String key1 = "Best key123_ever+:.!";
        String key2 = "Another best2431";
        int seed = 1231238;

        return Arrays
                .stream(DefaultHasher.values())
                .map(hasher -> dynamicTest(
                        hasher.name(),
                        () -> assertNotEquals(hasher.hash(key1, seed), hasher.hash(key2, seed)))
                );
    }
}
