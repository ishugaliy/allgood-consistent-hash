package org.ishugaliy.consistent.hash.hasher;

@FunctionalInterface
public interface Hasher {

    long hash(String key, int seed);
}
