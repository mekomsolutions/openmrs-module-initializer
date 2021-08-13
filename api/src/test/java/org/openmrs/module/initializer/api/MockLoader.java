package org.openmrs.module.initializer.api;

import java.util.List;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.loaders.BaseLoader;

public class MockLoader extends BaseLoader {
	
	private Domain domain;
	
	private boolean throwException = false;
	
	private int numberOfTimesLoadUnsafeCompleted = 0;
	
	public MockLoader(Domain domain) {
		this.domain = domain;
	}
	
	/**
	 * @param domain the domain that this loader should represent
	 * @param throwException set to true to throw an Exception during the loading process, to simulate
	 *            this behavior
	 */
	public MockLoader(Domain domain, boolean throwException) {
		this(domain);
		this.throwException = throwException;
	}
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		if (doThrow && throwException) {
			throw new RuntimeException("ERROR IN MOCK LOADER");
		}
		System.out.println("Method load() invoked on mock loader for domain '" + getDomainName() + "'.");
		numberOfTimesLoadUnsafeCompleted++;
	}
	
	@Override
	protected Domain getDomain() {
		return domain;
	}
	
	public int getNumberOfTimesLoadUnsafeCompleted() {
		return numberOfTimesLoadUnsafeCompleted;
	}
}
