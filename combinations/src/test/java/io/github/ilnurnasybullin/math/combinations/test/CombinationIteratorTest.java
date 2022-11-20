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

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Test cases for {@link Combination#iterator()}
 */
public class CombinationIteratorTest {

    @ParameterizedTest
    @MethodSource({
            "_emptyDataSet",
            "_singleDataSet",
            "_twoDataSet",
            "_threeDataSet",
            "_fourDataSet",
            "_fiveDataSet"
    })
    public <T> void unorderedCombinationTest_Success(Set<T> elements, int k,
                                                     Set<Set<T>> expectedCombinations) {
        for (Set<T> combinations: getUnorderedCombination(elements, k)) {
            Assertions.assertTrue(expectedCombinations.remove(combinations));
        }

        Assertions.assertTrue(expectedCombinations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("_combinationIteratorNext_Exception")
    public <T, X extends Exception> void combinationIteratorNext_Exception(Supplier<ICombination<T>> construct,
                                                                           Class<X> expectedException) {
        Iterator<Set<T>> iterator = construct.get().iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }

        Assertions.assertThrows(expectedException, iterator::next);
    }

    private <T> ICombination<T> getUnorderedCombination(Set<T> elements, int k) {
        return Combination.unordered(elements, k);
    }

    public static Stream<Arguments> _combinationIteratorNext_Exception() {
        Class<NoSuchElementException> expectedException = NoSuchElementException.class;

        Supplier<ICombination<Integer>> construct_1 = () -> Combination.unordered(Set.of(), 0);
        Supplier<ICombination<String>> construct_2 = () -> Combination.unordered(Set.of("1", "2"), 1);

        return Stream.of(
                Arguments.of(construct_1, expectedException),
                Arguments.of(construct_2, expectedException)
        );
    }

    @SafeVarargs
    private static <T> Set<Set<T>> setOf(Set<T> ... sets) {
        Set<Set<T>> set = new HashSet<>();
        Collections.addAll(set, sets);
        return set;
    }

    public static Stream<Arguments> _emptyDataSet() {
        Set<Object> elements = Collections.emptySet();
        int k = 0;

        Set<Set<Object>> expectedCombinations = setOf(Collections.emptySet());
        return Stream.of(Arguments.of(
                elements, k, expectedCombinations
        ));
    }

    public static Stream<Arguments> _singleDataSet() {
        Set<String> elements = Set.of("some");

        Set<Set<String>> expectedCombinations_0 = setOf(Collections.emptySet());
        Set<Set<String>> expectedCombinations_1 = setOf(Set.of("some"));

        return Stream.of(
                Arguments.of(elements, 0, expectedCombinations_0),
                Arguments.of(elements, 1, expectedCombinations_1)
        );
    }

    public static Stream<Arguments> _twoDataSet() {
        Set<Double> elements = Set.of(1.2d, -3.4d);

        Set<Set<Double>> expectedCombinations_0 = setOf(Collections.emptySet());
        Set<Set<Double>> expectedCombinations_1 = setOf(
                Set.of(1.2d), Set.of(-3.4d)
        );

        Set<Set<Double>> expectedCombinations_2 = setOf(
                Set.of(1.2d, -3.4d)
        );

        return Stream.of(
                Arguments.of(elements, 0, expectedCombinations_0),
                Arguments.of(elements, 1, expectedCombinations_1),
                Arguments.of(elements, 2, expectedCombinations_2)
        );
    }

    public static Stream<Arguments> _threeDataSet() {
        Set<Integer> elements = Set.of(1, 2, 3);

        Set<Set<Integer>> expectedCombinations_0 = setOf(Collections.emptySet());
        Set<Set<Integer>> expectedCombinations_1 = setOf(
                Set.of(1), Set.of(2), Set.of(3)
        );
        Set<Set<Integer>> expectedCombinations_2 = setOf(
                Set.of(1, 2), Set.of(1, 3), Set.of(2, 3)
        );
        Set<Set<Integer>> expectedCombinations_3 = setOf(
                Set.of(1, 2, 3)
        );

        return Stream.of(
                Arguments.of(elements, 0, expectedCombinations_0),
                Arguments.of(elements, 1, expectedCombinations_1),
                Arguments.of(elements, 2, expectedCombinations_2),
                Arguments.of(elements, 3, expectedCombinations_3)
        );
    }

    public static Stream<Arguments> _fourDataSet() {
        Set<Integer> elements = Set.of(1, 2, 3, 4);

        Set<Set<Integer>> expectedCombinations_0 = setOf(Collections.emptySet());
        Set<Set<Integer>> expectedCombinations_1 = setOf(
                Set.of(1), Set.of(2), Set.of(3), Set.of(4)
        );
        Set<Set<Integer>> expectedCombinations_2 = setOf(
                Set.of(1, 2), Set.of(1, 3), Set.of(1, 4),
                Set.of(2, 3), Set.of(2, 4),
                Set.of(3, 4)
        );
        Set<Set<Integer>> expectedCombinations_3 = setOf(
                Set.of(1, 2, 3), Set.of(1, 2, 4),
                Set.of(1, 3, 4), Set.of(2, 3, 4)
        );

        Set<Set<Integer>> expectedCombinations_4 = setOf(
                Set.of(1, 2, 3, 4)
        );

        return Stream.of(
                Arguments.of(elements, 0, expectedCombinations_0),
                Arguments.of(elements, 1, expectedCombinations_1),
                Arguments.of(elements, 2, expectedCombinations_2),
                Arguments.of(elements, 3, expectedCombinations_3),
                Arguments.of(elements, 4, expectedCombinations_4)
        );
    }

    public static Stream<Arguments> _fiveDataSet() {
        Set<Integer> elements = Set.of(1, 2, 3, 4, 5);

        Set<Set<Integer>> expectedCombinations_0 = setOf(Collections.emptySet());
        Set<Set<Integer>> expectedCombinations_1 = setOf(
                Set.of(1), Set.of(2), Set.of(3), Set.of(4), Set.of(5)
        );
        Set<Set<Integer>> expectedCombinations_2 = setOf(
                Set.of(1, 2), Set.of(1, 3), Set.of(1, 4), Set.of(1, 5),
                Set.of(2, 3), Set.of(2, 4), Set.of(2, 5),
                Set.of(3, 4), Set.of(3, 5),
                Set.of(4, 5)
        );
        Set<Set<Integer>> expectedCombinations_3 = setOf(
                Set.of(1, 2, 3), Set.of(1, 2, 4), Set.of(1, 2, 5),
                Set.of(1, 3, 4), Set.of(1, 3, 5), Set.of(1, 4, 5),
                Set.of(2, 3, 4), Set.of(2, 3, 5), Set.of(2, 4, 5),
                Set.of(3, 4, 5)
        );
        Set<Set<Integer>> expectedCombinations_4 = setOf(
                Set.of(1, 2, 3, 4), Set.of(1, 2, 3, 5), Set.of(1, 2, 4, 5),
                Set.of(1, 3, 4, 5), Set.of(2, 3, 4, 5)
        );
        Set<Set<Integer>> expectedCombinations_5 = setOf(
                Set.of(1, 2, 3, 4, 5)
        );

        return Stream.of(
                Arguments.of(elements, 0, expectedCombinations_0),
                Arguments.of(elements, 1, expectedCombinations_1),
                Arguments.of(elements, 2, expectedCombinations_2),
                Arguments.of(elements, 3, expectedCombinations_3),
                Arguments.of(elements, 4, expectedCombinations_4),
                Arguments.of(elements, 5, expectedCombinations_5)
        );
    }

}
