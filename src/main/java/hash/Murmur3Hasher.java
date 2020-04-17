package hash;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

@SuppressWarnings("UnstableApiUsage")
public final class Murmur3Hasher implements Hasher {

    private final HashFunction defaultHashFunction;

    public Murmur3Hasher() {
        this.defaultHashFunction = Hashing.murmur3_128(0);
    }

    @Override
    public long hash(String key, int seed) {
        HashFunction hasher = seed != 0 ? Hashing.murmur3_128(seed) : defaultHashFunction;
        return hasher
                .hashString(key, Charset.defaultCharset())
                .asLong();
    }
}
