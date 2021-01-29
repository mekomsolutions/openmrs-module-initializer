package org.openmrs.module.initializer.api.loaders;

import java.util.List;

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
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		
	}
	
	@Override
	public void load(List<String> wildcardExclusions) {
		
	}
	
	@Override
	public void load() {
		
	}
	
}
