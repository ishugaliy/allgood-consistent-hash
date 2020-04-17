package hash;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

@SuppressWarnings("UnstableApiUsage")
public class MD5Hasher implements Hasher {

    private final HashFunction hashFunction;

    public static void main(String[] args) {
        new MD5Hasher().hash("dasa", 12);
    }

    public MD5Hasher() {
        this.hashFunction = Hashing.md5();
    }

    @Override
    public long hash(String key, int seed) {
        return hashFunction
                .hashString(key + ":" + seed, Charset.defaultCharset())
                .asLong();
    }
}
