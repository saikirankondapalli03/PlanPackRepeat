package com.cucumberFramework.pojo;


import java.io.Closeable;

public class Calculator implements Closeable {

	public int add(int a, int b) {
		return a + b;
	}

	public int subtract(int a, int b) {
		return a -b;
	}

	@Override
	public void close() {
		// Some hypothetical close function
	}
}