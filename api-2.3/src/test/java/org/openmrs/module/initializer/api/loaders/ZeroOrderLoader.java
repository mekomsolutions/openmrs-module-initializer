package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.ConfigDirUtil;

public class ZeroOrderLoader implements Loader {
	
	@Override
	public int compareTo(Loader that) {
		return this.getOrder().compareTo(that.getOrder());
	}
	
	@Override
	public String getDomainName() {
		return "Test Domain";
	}
	
	@Override
	public Integer getOrder() {
		return 0;
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return null;
	}
	
	@Override
	public void load() {
	}
	
}
