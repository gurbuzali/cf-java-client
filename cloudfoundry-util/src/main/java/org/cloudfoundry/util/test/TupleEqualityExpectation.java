/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.util.test;

import org.cloudfoundry.util.tuple.TupleUtils;
import reactor.test.ScriptedSubscriber;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public final class TupleEqualityExpectation {

    public static <T> ScriptedSubscriber<Tuple2<T, T>> create() {
        return ScriptedSubscriber.<Tuple2<T, T>>create()
            .expectValueWith(predicate(), assertionMessage())
            .expectComplete();
    }

    private static <T> Function<Tuple2<T, T>, String> assertionMessage() {
        return TupleUtils.function((expected, actual) -> String.format("expected value: %s; actual value: %s", expected, actual));
    }

    private static <T> Predicate<Tuple2<T, T>> predicate() {
        return TupleUtils.predicate((expected, actual) -> expected.getClass().isArray() ? Arrays.equals((Object[]) expected, (Object[]) actual) : expected.equals(actual));
    }

}
