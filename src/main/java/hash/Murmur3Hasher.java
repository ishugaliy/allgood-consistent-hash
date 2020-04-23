package hash;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

@SuppressWarnings("UnstableApiUsage")
public final class Murmur3Hasher implements Hasher {

    @Override
    public long hash(String key, int seed) {
        HashFunction hasher = seed != 0 ? Hashing.murmur3_128(seed) : Hashing.murmur3_128();
        return hasher
                .hashString(key, Charset.defaultCharset())
                .asLong();
    }
}
