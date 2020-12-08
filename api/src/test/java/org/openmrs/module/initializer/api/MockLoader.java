package org.openmrs.module.initializer.api;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.loaders.BaseLoader;

public class MockLoader extends BaseLoader {
	
	private Domain domain;
	
	public MockLoader(Domain domain) {
		this.domain = domain;
	}
	
	@Override
	public void load() {
		System.out.println("load() invoked on mock loader for domain " + getDomainName() + ".");
	}
	
	@Override
	protected Domain getDomain() {
		return domain;
	}
	
}
