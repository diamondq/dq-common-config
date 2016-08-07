package com.diamondq.common.config.core;

import com.diamondq.common.config.ConfigKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListBuilderFactory {

	public static class ListBuilder {

		private List<Object> mList;

		public ListBuilder() {
			mList = new ArrayList<>();
		}

		@ConfigKey("*")
		public void set(Object pValue) {
			mList.add(pValue);
		}

		public List<?> build() {
			return Collections.unmodifiableList(mList);
		}
	}

	public static ListBuilder builder() {
		return new ListBuilder();
	}
}
