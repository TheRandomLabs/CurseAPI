package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.MapUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.IOUtils;
import com.therandomlabs.utils.misc.ReflectionUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.throwable.ThrowableHandling;

public final class Utils {
	private Utils() {}

	public static <T> T fromJson(Path path, Class<T> clazz) throws IOException {
		return new Gson().fromJson(IOUtils.readFile(path), clazz);
	}

	public static ZonedDateTime parseTime(String time) {
		try {
			return ZonedDateTime.parse(time);
		} catch(DateTimeParseException ex) {
			try {
				//Probably an epoch
				return parseTime(Long.parseLong(time));
			} catch(NumberFormatException ex2) {
				//CurseMeta compat
				if(StringUtils.lastChar(time) != 'Z') {
					return ZonedDateTime.parse(time + 'Z');
				}

				throw ex;
			}
		}
	}

	public static ZonedDateTime parseTime(long epochSeconds) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
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
