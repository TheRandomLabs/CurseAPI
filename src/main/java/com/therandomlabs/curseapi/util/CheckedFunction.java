package com.therandomlabs.curseapi.util;

@FunctionalInterface
public interface CheckedFunction<I, R, T extends Throwable> {
	R apply(I input) throws T;
}
