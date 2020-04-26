package org.ishugaliy.consistent.hash.hasher;

import net.openhft.hashing.LongHashFunction;

import java.util.function.Function;

public enum DefaultHasher implements Hasher {
    /**
     * https://github.com/aappleby/smhasher/wiki/MurmurHash3
     */
    MURMUR_3(LongHashFunction::murmur_3),

    /**
     * https://github.com/google/cityhash
     */
    CITY_HASH(LongHashFunction::city_1_1),

    /**
     * https://github.com/google/farmhash
     */
    FARM_HASH(LongHashFunction::farmUo),

    /**
     * https://github.com/jandrewrogers/MetroHash
     */
    METRO_HASH(LongHashFunction::metro),

    /**
     * https://github.com/wangyi-fudan/wyhash
     */
    WY_HASH(LongHashFunction::wy_3),

    /**
     * https://github.com/Cyan4973/xxHash
     */
    XX_HASH(LongHashFunction::xx);

    private final Function<Integer, LongHashFunction> buildHashFunction;

    DefaultHasher(Function<Integer, LongHashFunction> buildHashFunction) {
        this.buildHashFunction = buildHashFunction;
    }

    @Override
    public long hash(String key, int seed) {
        return buildHashFunction.apply(seed).hashChars(key);
    }
}
