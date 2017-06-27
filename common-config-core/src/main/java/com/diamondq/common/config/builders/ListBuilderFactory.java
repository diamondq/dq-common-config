package com.diamondq.common.config.builders;

import com.diamondq.common.config.ConfigKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A general factory for List's. This mostly exists to provide a consistent interface in case someone uses a factory for
 * lists.
 */
public abstract class ListBuilderFactory {

	/**
	 * The builder for the factory
	 */
	public static class ListBuilder {

		private List<Object> mList;

		/**
		 * The constructor
		 */
		private ListBuilder() {
			mList = new ArrayList<>();
		}

		/**
		 * The wildcard set. Any 'child' is automatically bound here.
		 * 
		 * @param pValue
		 */
		@ConfigKey("*")
		public void set(Object pValue) {
			mList.add(pValue);
		}

		/**
		 * Builds the list
		 * 
		 * @return the list
		 */
		public List<?> build() {
			return Collections.unmodifiableList(mList);
		}
	}

	/**
	 * Generates a builder
	 * 
	 * @return the builder
	 */
	public static ListBuilder builder() {
		return new ListBuilder();
	}
}
