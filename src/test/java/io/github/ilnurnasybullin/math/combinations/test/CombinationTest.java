/*
 * Copyright 2022 Ilnur Nasybullin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ilnurnasybullin.math.combinations.test;

import io.github.ilnurnasybullin.math.combinations.Combination;
import io.github.ilnurnasybullin.math.combinations.ICombination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Test class for {@link Combination}
 */
public class CombinationTest {

    @ParameterizedTest
    @MethodSource("_testUnordered_Success_DataSet")
    public <T> void testUnordered_Success(Set<T> elements, int k) {
        ICombination<T> combination = Combination.unordered(elements, k);
        Assertions.assertNotNull(combination);
    }

    public static Stream<Arguments> _testUnordered_Success_DataSet() {
        Set<Integer> numbers = Set.of(0, 1, 2, 3, 4, 5);
        Set<?> strings = Collections.emptySet();
        Set<Double> doubles = Collections.singleton(4.5d);

        return Stream.of(
                Arguments.of(strings, 0),
                Arguments.of(doubles, 0),
                Arguments.of(doubles, 1),
                Arguments.of(numbers, 0),
                Arguments.of(numbers, 1),
                Arguments.of(numbers, 3),
                Arguments.of(numbers, 4),
                Arguments.of(numbers, numbers.size()),
                Arguments.of(Set.of(), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("_testUnordered_Exception_DataSet")
    public <T, X extends Exception> void testUnordered_Exception(Set<T> elements, int k,
                                                                 Class<X> expectedException) {
        Assertions.assertThrows(expectedException, () -> Combination.unordered(elements, k));
    }

    public static Stream<Arguments> _testUnordered_Exception_DataSet() {
        return Stream.of(
                Arguments.of(null, 0, NullPointerException.class),
                Arguments.of(Set.of(), -1, IllegalArgumentException.class),
                Arguments.of(Set.of(4), 2, IllegalArgumentException.class)
        );
    }

    private static <T> Supplier<ICombination<T>> unordered(Set<T> elements, int k) {
        return () -> Combination.unordered(elements, k);
    }
}
