package com.diamondq.common.config.tests.model;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Test Factory
 */
public class TestFactory {

	/**
	 * Creates the builder
	 * 
	 * @return the builder
	 */
	public static TestFactoryBuilder builder() {
		return new TestFactoryBuilder();
	}

	/**
	 * Creates the builder
	 * 
	 * @param pArg the argument
	 * @return the builder
	 */
	public static TestFactoryBuilder builder(String pArg) {
		return new TestFactoryBuilder(pArg);
	}

	/**
	 * Builder for TestFactory
	 */
	public static class TestFactoryBuilder {

		@Nullable
		private String	mFile;

		@Nullable
		private String	mArg;

		private TestFactoryBuilder() {

		}

		private TestFactoryBuilder(String pArg) {
			mArg = pArg;
		}

		/**
		 * Sets the file
		 * 
		 * @param pValue the file
		 * @return the builder
		 */
		public TestFactoryBuilder file(String pValue) {
			mFile = pValue;
			return this;
		}

		/**
		 * Builds a new TestObject
		 * 
		 * @return the TestObject
		 */
		public TestObject build() {
			TestObject r = new TestObject();
			r.file = mFile;
			r.arg = mArg;
			return r;
		}
	}

	/**
	 * Test object
	 */
	public static class TestObject {
		/**
		 * The argument
		 */
		@Nullable
		public String	arg;

		/**
		 * The file
		 */
		@Nullable
		public String	file;

		/**
		 * Default constructor
		 */
		public TestObject() {

		}
	}
}
