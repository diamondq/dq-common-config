package com.diamondq.common.config.inject;

import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Priority;

/**
 * Injection/CDI helper
 */
public class InjectUtils {

	private static final ConcurrentMap<Class<?>, Integer> sClassToPriorityStringMap = new ConcurrentHashMap<>();

	/**
	 * Reorders a Iterable of entries based on their Priority
	 * 
	 * @param <T> the entry type
	 * @param pIterable the entries
	 * @return the sorted collection
	 */
	public static <T> Collection<T> orderByPriority(Iterable<T> pIterable) {
		TreeMap<String, T> m = new TreeMap<>();

		for (T val : pIterable) {
			Class<?> c = val.getClass();
			Integer priorityKey = sClassToPriorityStringMap.get(c);
			if (priorityKey == null) {
				Priority priority = c.getAnnotation(Priority.class);
				if (priority == null)
					priorityKey = Integer.MAX_VALUE;
				else
					priorityKey = priority.value();
				sClassToPriorityStringMap.put(c, priorityKey);
			}
			m.put(String.format("%010d-%s@%s", priorityKey, c.getName(),
				Integer.toHexString(System.identityHashCode(val))), val);
		}

		return m.values();
	}
}
