package org.openmrs.module.initializer.api;

import java.util.Arrays;

import org.openmrs.BaseOpenmrsObject;

/**
 * An instance is groups a {@link BaseOpenmrsObject} and its CSV line.
 */
public class Instance<T extends BaseOpenmrsObject> {
	
	private T obj;
	
	private String[] line;
	
	public Instance(T obj, String[] line) {
		this.obj = obj;
		this.line = line;
	}
	
	public T getObject() {
		return obj;
	}
	
	public String[] getLine() {
		return line;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(line);
	}
}
