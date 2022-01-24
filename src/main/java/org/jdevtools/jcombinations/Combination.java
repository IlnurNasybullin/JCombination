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

import org.jdevtools.factorial.Factorials;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;

/**
 * Implementation of interface {@link ICombination}. <b>This is a thread-safety class, but iterators (and spliterators) can
 * be thread-unsafe</b>.Thread-safety is guaranteed by immutability of internal states of this class (all fields are final).
 * @implNote On current moment, this class has only one implementation of iterator - {@link ChaseSequenceIterator}. In future
 * versions it's supposed to create an ordered iterator and effective ordered spliterator.
 * @param <T> - type of set's element
 */
public class Combination<T> implements ICombination<T>, Iterable<Set<T>> {

    /**
     * This class is a <b>thread-unsafe</b> implementation of <b>unordered</b> iterator.
     * @implSpec Implementation of the iteration algorithm (Chase Sequence) is completely copied from Donald E. Knuth's book
     * "The Art of Computer Programming, Volume 4, Fascicle 3 - Generating All Combinations and Partitions" (2005,
     * publisher - Addison-Wesley Professional).
     * @implNote This class is static for avoiding memory leaks (when iterable object is no longer needed, but his iterator
     * is used; every inner non-static object has reference on outer object that may cause a memory leak).
     * @param <T> - type of set's element
     */
    private static class ChaseSequenceIterator<T> implements Iterator<Set<T>> {

        /**
         * Elements of a combinatorial set
         */
        private final T[] elements;

        /**
         * Combinatorial set's length
         */
        private final int k;

        private final BitSet a;
        private final BitSet w;

        private int r;

        /**
         * For fulfill the first condition {@link #hasNext()} even for empty set (empty array of {@link #elements}).
         */
        private int j = -1;

        private ChaseSequenceIterator(T[] elements, int k) {
            this.elements = elements;
            this.k = k;
            a = firstIterCombination(elements.length, k);
            w = initW(elements.length);
            r = initR(elements.length, k);
        }

        private int initR(int n, int k) {
            return k == n ? k : n - k;
        }

        private BitSet initW(int nbits) {
            BitSet bitSet = new BitSet(nbits);
            bitSet.set(0, nbits);
            return bitSet;
        }

        private BitSet firstIterCombination(int n, int k) {
            BitSet bitSet = new BitSet(n);
            bitSet.set(n - k, n);
            return bitSet;
        }

        /**
         * Predicate of the existing of next combination. It's guaranteed that first calling of predicate will return true
         * @return true if has next combination
         * @see #j
         */
        @Override
        public boolean hasNext() {
            return j != elements.length;
        }

        /**
         * Set of elements is a combination {@link #k} elements from n-length {@link #elements array}.
         * @return set of elements is a combination {@link #k} elements from n-length {@link #elements array}.
         * @throws NoSuchElementException if iterator has no more combinations
         */
        @Override
        public Set<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("This iterator has no more combinations!");
            }

            Set<T> combination = combination();
            searchAndBranching();
            return combination;
        }

        private void searchAndBranching() {
            j = r;
            while (!w.get(j) && j < elements.length) {
                w.set(j);
                j++;
            }

            if (!hasNext()) {
                return;
            }

            w.clear(j);

            boolean jIsEven = j % 2 == 0;
            boolean jIsZero = !a.get(j);

            if (!jIsEven && !jIsZero) {
                shiftRightOnOne();
            } else if (jIsEven && !jIsZero) {
                shiftRightOnTwo();
            } else if (jIsEven) {
                shiftLeftOnOne();
            } else {
                shiftLeftOnTwo();
            }
        }

        private void shiftLeftOnTwo() {
            if (a.get(j - 1)) {
                shiftLeftOnOne();
            } else {
                a.set(j);
                a.clear(j - 2);
                if (r == j - 2) {
                    r = j;
                } else if (r == j - 1) {
                    r = j - 2;
                }
            }
        }

        private void shiftLeftOnOne() {
            a.set(j);
            a.clear(j - 1);
            if (r == j && r > 1) {
                r = j - 1;
            } else if (r == j - 1) {
                r = j;
            }
        }

        private void shiftRightOnTwo() {
            if (a.get(j - 2)) {
                shiftRightOnOne();
            } else {
                a.set(j - 2);
                a.clear(j);
                if (r == j) {
                    r = Integer.max(j - 2, 1);
                } else if (r == j - 2) {
                    r = j - 1;
                }
            }
        }

        private void shiftRightOnOne() {
            a.set(j - 1);
            a.clear(j);
            if (r == j && r > 1) {
                r = j - 1;
            } else if (r == j - 1) {
                r = j;
            }
        }

        private Set<T> combination() {
            Set<T> combination = new HashSet<>(k);
            for (int i = 0; i < elements.length; i++) {
                if (a.get(i)) {
                    combination.add(elements[i]);
                }
            }

            return combination;
        }
    }

    /**
     * Elements of a combinatorial set
     */
    private final T[] elements;

    /**
     * Comparator of {@link #elements}.
     * @implNote On current version comparator isn't used, but for future version of {@link Combination} comparator will
     * be used for ordered iterator and spliterator.
     */
    private final Comparator<? super T> comparator;

    /**
     * Combinatorial set's length
     */
    private final int k;

    private Combination(T[] elements, Comparator<? super T> comparator, int k) {
        this.elements = elements;
        this.comparator = comparator;
        this.k = k;
    }

    /**
     * Static constructor of {@link Combination}. As an iterator, will be used unordered {@link ChaseSequenceIterator}.
     * @param elements - set of elements for combinatorial set
     * @param elementType - class type of set's element
     * @param k - combinatorial set's length
     * @param <T> - type of set's element
     * @return {@link Combination} with unordered iterator
     * @throws NullPointerException if elements or elementType is null
     * @throws IllegalArgumentException if elements' size &lt; k or k &lt; 0
     * @throws ArrayStoreException if elements' type is not casting with elementType
     * @implNote array from elements copied with method {@link Set#toArray(Object[])}
     */
    public static <T> Combination<T> unordered(Set<T> elements, Class<T> elementType, int k) {
        checkLength(elements, k);
        T[] array = getArray(elements, elementType);
        return new Combination<>(array, null, k);
    }

    private static <T> T[] getArray(Collection<T> elements, Class<T> elementType) {
        T[] array = wrapUncheckedArray(Array.newInstance(elementType, elements.size()));
        elements.toArray(array);
        return array;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] wrapUncheckedArray(Object array) {
        return (T[]) array;
    }

    private static <T> void checkLength(Set<T> elements, int k) {
        int size = elements.size();
        if (size < k) {
            throw new IllegalArgumentException(String.format("k is more, than elements' size (%d)!", size));
        }

        if (k < 0) {
            throw new IllegalArgumentException("k must be non negative number!");
        }
    }

    /**
     * Return size of all combinations of n by k as a long type. If size more than {@link Long#MAX_VALUE} returning empty
     * {@link Optional#empty() empty Optional value}.
     * @return size of all combinations of n by k as a long type.
     * @implSpec for calculating longSize is called {@link #size()}
     */
    @Override
    public Optional<Long> longSize() {
        BigInteger size = size();
        return size.bitLength() <= 63 ? Optional.of(size.longValue()) : Optional.empty();
    }

    /**
     * Return size of all combinations of n by k as a {@link BigInteger} type.
     * @return size of all combinations of n by k as a {@link BigInteger} type.
     * @implSpec for calculating combinations of n by k is used {@link Factorials#combinations(int, int)}
     */
    @Override
    public BigInteger size() {
        return Factorials.combinations(elements.length, k);
    }

    /**
     * Return iterator for combinations of {@link #elements}.
     * @return iterator for combinations of {@link #elements}.
     * @implSpec In current version of {@link Combination} will return only one implementation of iterator - {@link ChaseSequenceIterator}.
     * But in future version will be returning depending on {@link #comparator}.
     */
    @Override
    public Iterator<Set<T>> iterator() {
        return new ChaseSequenceIterator<>(elements, k);
    }

    /**
     * Return spliterator for combinations of {@link #elements}.
     * @return spliterator for combinations of {@link #elements}.
     * @implSpec In current version of {@link Combination} will be return {@link Iterable#spliterator() default implementation}
     * of spliterator. But in future version will be returning more effective spliterator based on combinatorial number system.
     */
    @Override
    public Spliterator<Set<T>> spliterator() {
        return ICombination.super.spliterator();
    }
}
