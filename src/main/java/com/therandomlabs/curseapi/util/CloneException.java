package com.therandomlabs.curseapi.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.MapUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.ReflectionUtils;
import com.therandomlabs.utils.throwable.ThrowableHandling;

public class CloneException extends RuntimeException {
	private static final long serialVersionUID = -1718467136961470352L;

	public CloneException(Class<?> callerClass) {
		super(String.format(
				"An error occurred while cloning a %s instance. This should not have occurred.",
				callerClass.getSimpleName()));
	}

	public static <E extends Cloneable> E[] tryClone(E[] array) {
		try {
			return ArrayUtils.clone(array);
		} catch(Exception ex) {
			try {
				throw new CloneException(ReflectionUtils.getCallerClass());
			} catch(ClassNotFoundException ex2) {
				ThrowableHandling.handle(ex2);
			}
		}

		return null;
	}

	public static <E extends Cloneable> TRLList<E> tryClone(Collection<E> list) {
		try {
			return CollectionUtils.clone(list);
		} catch(Exception ex) {
			try {
				throw new CloneException(ReflectionUtils.getCallerClass());
			} catch(ClassNotFoundException ex2) {
				ThrowableHandling.handle(ex2);
			}
		}

		return null;
	}

	public static <E extends Cloneable> KeySetView<E, Boolean> tryClone(
			KeySetView<E, Boolean> keySetView, boolean deepClone) {
		try {
			if(deepClone) {
				return CollectionUtils.deepCloneKeySet(keySetView);
			}
			return CollectionUtils.cloneKeySet(keySetView);
		} catch(Exception ex) {
			try {
				throw new CloneException(ReflectionUtils.getCallerClass());
			} catch(ClassNotFoundException ex2) {
				ThrowableHandling.handle(ex2);
			}
		}

		return null;
	}

	public static <K, V> HashMap<K, V> tryClone(Map<K, V> map) {
		try {
			return MapUtils.clone(map);
		} catch(Exception ex) {
			try {
				throw new CloneException(ReflectionUtils.getCallerClass());
			} catch(ClassNotFoundException ex2) {
				ThrowableHandling.handle(ex2);
			}
		}

		return null;
	}
}
