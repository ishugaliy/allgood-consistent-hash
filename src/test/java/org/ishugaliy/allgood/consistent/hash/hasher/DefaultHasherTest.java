/*
 * The MIT License
 *
 * Copyright (c) 2020 Yuriy Shugaliy
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
