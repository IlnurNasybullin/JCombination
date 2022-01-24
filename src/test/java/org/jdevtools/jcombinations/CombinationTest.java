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

package org.jdevtools.jcombinations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Test class for {@link Combination}
 */
public class CombinationTest {

    @ParameterizedTest
    @MethodSource("_testUnordered_Success_DataSet")
    public <T> void testUnordered_Success(Set<T> elements, Class<T> elementType, int k) {
        ICombination<T> combination = Combination.unordered(elements, elementType, k);
        Assertions.assertNotNull(combination);
    }

    public static Stream<Arguments> _testUnordered_Success_DataSet() {
        Set<Integer> numbers = Set.of(0, 1, 2, 3, 4, 5);
        Set<?> strings = Collections.emptySet();
        Set<Double> doubles = Collections.singleton(4.5d);

        return Stream.of(
                Arguments.of(strings, Object.class, 0),
                Arguments.of(doubles, Double.class, 0),
                Arguments.of(doubles, Double.class, 1),
                Arguments.of(numbers, Integer.class, 0),
                Arguments.of(numbers, Integer.class, 1),
                Arguments.of(numbers, Integer.class, 3),
                Arguments.of(numbers, Integer.class, 4),
                Arguments.of(numbers, Integer.class, numbers.size()),
                Arguments.of(Set.of(), Void.class, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("_testUnordered_Exception_DataSet")
    public <T, X extends Exception> void testUnordered_Exception(Set<T> elements, Class<T> elementType, int k,
                                                                 Class<X> expectedException) {
        Assertions.assertThrows(expectedException, () -> Combination.unordered(elements, elementType, k));
    }

    public static Stream<Arguments> _testUnordered_Exception_DataSet() {
        return Stream.of(
                Arguments.of(null, Object.class, 0, NullPointerException.class),
                Arguments.of(Set.of(1), null, 1, NullPointerException.class),
                Arguments.of(Set.of(1), String.class, 0, ArrayStoreException.class),
                Arguments.of(Set.of("1", "2"), Void.class, 2, ArrayStoreException.class),
                Arguments.of(Set.of(), Object.class, -1, IllegalArgumentException.class),
                Arguments.of(Set.of(4), Integer.class, 2, IllegalArgumentException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("_testLongSize_Success_DataSet")
    public <T> void testLongSize_Success(Supplier<ICombination<T>> construct, Optional<Long> expectedLongSize) {
        Assertions.assertEquals(expectedLongSize, construct.get().longSize());
    }

    public static Stream<Arguments> _testLongSize_Success_DataSet() {
        Set<Integer> emptySet = Collections.emptySet();
        Set<Integer> singletonSet = Collections.singleton(1);
        Set<Integer> elements1 = IntStream.range(0, 15)
                .boxed().collect(Collectors.toUnmodifiableSet());
        Set<Integer> elements2 = IntStream.range(0, 50)
                .boxed().collect(Collectors.toUnmodifiableSet());

        Set<Integer> elements3 = IntStream.range(0, 100)
                .boxed().collect(Collectors.toUnmodifiableSet());

        Class<Integer> elementType = Integer.class;
        return Stream.of(
                Arguments.of(unordered(emptySet, elementType, 0), Optional.of(1L)),
                Arguments.of(unordered(singletonSet, elementType, 0), Optional.of(1L)),
                Arguments.of(unordered(singletonSet, elementType, 1), Optional.of(1L)),
                Arguments.of(unordered(elements1, elementType, 4), Optional.of(1_365L)),
                Arguments.of(unordered(elements1, elementType, 7), Optional.of(6_435L)),
                Arguments.of(unordered(elements2, elementType, 15), Optional.of(2_250_829_575_120L)),
                Arguments.of(unordered(elements2, elementType, 25), Optional.of(126_410_606_437_752L)),
                Arguments.of(unordered(elements3, elementType, 40), Optional.empty()),
                Arguments.of(unordered(elements3, elementType, 60), Optional.empty()),
                Arguments.of(unordered(elements3, elementType, 50), Optional.empty())
        );
    }

    private static <T> Supplier<ICombination<T>> unordered(Set<T> elements, Class<T> elementType, int k) {
        return () -> Combination.unordered(elements, elementType, k);
    }

    @ParameterizedTest
    @MethodSource("_testSize_Success_DataSet")
    public <T> void testSize_Success(Supplier<ICombination<T>> construct, BigInteger expectedSize) {
        Assertions.assertEquals(expectedSize, construct.get().size());
    }

    public static Stream<Arguments> _testSize_Success_DataSet() {
        Set<Integer> emptySet = Collections.emptySet();
        Set<Integer> singletonSet = Collections.singleton(1);
        Set<Integer> elements1 = IntStream.range(0, 15)
                .boxed().collect(Collectors.toUnmodifiableSet());
        Set<Integer> elements2 = IntStream.range(0, 50)
                .boxed().collect(Collectors.toUnmodifiableSet());

        Set<Integer> elements3 = IntStream.range(0, 100)
                .boxed().collect(Collectors.toUnmodifiableSet());

        Class<Integer> elementType = Integer.class;
        return Stream.of(
                Arguments.of(unordered(emptySet, elementType, 0), BigInteger.ONE),
                Arguments.of(unordered(singletonSet, elementType, 0), BigInteger.ONE),
                Arguments.of(unordered(singletonSet, elementType, 1), BigInteger.ONE),
                Arguments.of(unordered(elements1, elementType, 4), BigInteger.valueOf(1_365L)),
                Arguments.of(unordered(elements1, elementType, 7), BigInteger.valueOf(6_435L)),
                Arguments.of(unordered(elements2, elementType, 15), BigInteger.valueOf(2_250_829_575_120L)),
                Arguments.of(unordered(elements2, elementType, 25), BigInteger.valueOf(126_410_606_437_752L)),
                Arguments.of(unordered(elements3, elementType, 40), new BigInteger("13746234145802811501267369720")),
                Arguments.of(unordered(elements3, elementType, 60), new BigInteger("13746234145802811501267369720")),
                Arguments.of(unordered(elements3, elementType, 50), new BigInteger("100891344545564193334812497256"))
        );
    }
}
