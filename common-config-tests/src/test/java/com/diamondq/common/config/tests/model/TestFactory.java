package com.diamondq.common.config.tests.model;

public class TestFactory {

	public static TestFactoryBuilder builder() {
		return new TestFactoryBuilder();
	}

	public static TestFactoryBuilder builder(String pArg) {
		return new TestFactoryBuilder(pArg);
	}

	public static class TestFactoryBuilder {

		private String	mFile;

		private String	mArg;

		private TestFactoryBuilder() {

		}

		private TestFactoryBuilder(String pArg) {
			mArg = pArg;
		}

		public TestFactoryBuilder file(String pValue) {
			mFile = pValue;
			return this;
		}

		public TestObject build() {
			TestObject r = new TestObject();
			r.file = mFile;
			r.arg = mArg;
			return r;
		}
	}

	public static class TestObject {
		public String	arg;

		public String	file;

		public TestObject() {

		}
	}
}
