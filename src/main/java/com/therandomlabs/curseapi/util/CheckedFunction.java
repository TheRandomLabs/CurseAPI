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
