package hash;

import java.util.Random;

public class MD5Hash implements Hash {

    @Override
    public int hash(String key) {
        return new Random().nextInt(100_000);
    }
}
