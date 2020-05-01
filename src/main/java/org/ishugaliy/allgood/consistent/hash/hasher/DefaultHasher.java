/*
 * The MIT License
 *
 * Copyright (c) 2020 Iurii Shugalii
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ishugaliy.allgood.consistent.hash.hasher;

import net.openhft.hashing.LongHashFunction;

import java.util.function.Function;

/**
 * List of default hash functions.
 *
 * @author Iurii Shugalii
 */
public enum DefaultHasher implements Hasher {
    /**
     * @see <a href="https://github.com/aappleby/smhasher/wiki/MurmurHash3">Murmur3 Hash Docs</a>
     */
    MURMUR_3(LongHashFunction::murmur_3),

    /**
     * @see <a href="https://github.com/google/cityhash">City Hash Docs</a>
     */
    CITY_HASH(LongHashFunction::city_1_1),

    /***
     * @see <a href="https://github.com/google/farmhash">Farm Hash Docs</a>
     */
    FARM_HASH(LongHashFunction::farmUo),

    /**
     * @see <a href="https://github.com/jandrewrogers/MetroHash">Metro Hash Docs</a>
     */
    METRO_HASH(LongHashFunction::metro),

    /**
     * @see <a href="https://github.com/wangyi-fudan/wyhash">wyHash Docs</a>
     */
    WY_HASH(LongHashFunction::wy_3),

    /**
     * @see <a href="https://github.com/Cyan4973/xxHash">xxHash Docs</a>
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
