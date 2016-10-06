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

public final class ErrorExpectation {

    public static <T> ScriptedSubscriber<T> exact(Class<? extends Throwable> type, String format, Object... args) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(format, "format must not be null");

        String message = String.format(format, args);
        return ScriptedSubscriber.<T>create()
            .expectErrorWith(predicate(type, message), assertionMessage(type, message));
    }

    private static Function<Throwable, String> assertionMessage(Class<? extends Throwable> type, String message) {
        return t -> {
            if (!type.isInstance(t)) {
                return String.format("expected error of type: %s; actual type: %s", type.getSimpleName(), t.getClass().getSimpleName());
            }

            if (!message.equals(t.getMessage())) {
                return String.format("expected message: %s; actual message: %s", message, t.getMessage());
            }

            throw new IllegalArgumentException("Cannot generate assertion message for matching error");
        };
    }

    private static Predicate<Throwable> predicate(Class<? extends Throwable> type, String message) {
        return t -> type.isInstance(t) && message.equals(t.getMessage());
    }

}
