package hash;

public interface Hasher {

    long hash(String partitionKey);

    long hash(String partitionKey, int seed);
}
