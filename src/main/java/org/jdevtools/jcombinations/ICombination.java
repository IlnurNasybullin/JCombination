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

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for iteration of all combinations of n by k of some set of elements.
 * @param <T> - type of set's element
 */
public interface ICombination<T> extends Iterable<Set<T>> {

    /**
     * Return size of all combinations of n by k as a long type. If size more than {@link Long#MAX_VALUE} returning empty
     * {@link Optional#empty() empty Optional value}.
     * @return size of all combinations of n by k as a long type.
     */
    Optional<Long> longSize();

    /**
     * Return size of all combinations of n by k as a {@link BigInteger} type.
     * @return size of all combinations of n by k as a {@link BigInteger} type.
     */
    BigInteger size();

}
