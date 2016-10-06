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

import org.springframework.util.Assert;
import reactor.test.ScriptedSubscriber;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ErrorExpectation {

    public static <T> ScriptedSubscriber<T> exact(Class<? extends Throwable> type, String format, Object... args) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(format, "format must not be null");

        String expected = String.format(format, args);
        return ScriptedSubscriber.<T>create()
            .expectErrorWith(predicate(type, expected::equals), assertionMessage(type, expected::equals, actual -> String.format("expected message: %s; actual message: %s", expected, actual)));
    }

    public static <T> ScriptedSubscriber<T> matches(Class<? extends Throwable> type, String pattern) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(pattern, "pattern must not be null");

        Pattern expected = Pattern.compile(pattern);
        return ScriptedSubscriber.<T>create()
            .expectErrorWith(predicate(type, actual -> expected.matcher(actual).matches()), assertionMessage(type, actual -> expected.matcher(actual).matches(),
                actual -> String.format("expected message pattern: %s; actual message: %s", pattern, actual)));
    }

    private static Function<Throwable, String> assertionMessage(Class<? extends Throwable> type, Predicate<String> messagePredicate, Function<String, String> messageAssertionMessage) {
        return t -> {
            if (!type.isInstance(t)) {
                return String.format("expected error of type: %s; actual type: %s", type.getSimpleName(), t.getClass().getSimpleName());
            }

            if (!messagePredicate.test(t.getMessage())) {
                return messageAssertionMessage.apply(t.getMessage());
            }

            throw new IllegalArgumentException("Cannot generate assertion message for matching error");
        };
    }

    private static Predicate<Throwable> predicate(Class<? extends Throwable> type, Predicate<String> predicate) {
        return t -> type.isInstance(t) && predicate.test(t.getMessage());
    }

}
