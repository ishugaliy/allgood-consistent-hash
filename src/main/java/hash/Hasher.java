package hash;

@FunctionalInterface
public interface Hasher {

    long hash(String key, int seed);
}
