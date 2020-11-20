package org.openmrs.module.initializer.validator;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.loaders.BaseLoader;

public class TestLoader extends BaseLoader {
	
	private Domain domain;
	
	public TestLoader(Domain domain) {
		this.domain = domain;
	}
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Domain getDomain() {
		return domain;
	}
	
}
