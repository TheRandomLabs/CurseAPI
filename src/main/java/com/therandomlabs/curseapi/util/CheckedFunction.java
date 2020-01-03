/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.therandomlabs.curseapi.util;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link java.util.function.Function}-like interface which allows for throwing
 * checked {@link Exception}s.
 *
 * @param <T> the type of the input.
 * @param <R> the type of the output.
 * @param <E> the type of the checked {@link Exception}.
 */
@FunctionalInterface
public interface CheckedFunction<T, R, E extends Exception> {
	/**
	 * Applies this function to the specified argument.
	 *
	 * @param t an argument.
	 * @return the function result.
	 * @throws E if the function throws a checked {@link Exception} of type {@link E}.
	 */
	@Nullable
	R apply(T t) throws E;
}
