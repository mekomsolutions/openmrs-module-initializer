package org.openmrs.module.initializer.api.loaders;

import java.util.Collections;
import java.util.List;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all loaders. In all likelihood you should not subclass this class directly, rather
 * 
 * @see BaseFileLoader
 * @see BaseInputStreamLoader
 * @see {@link Loader}
 */
public abstract class BaseLoader implements Loader {
	
	@Autowired
	protected InitializerService iniz;
	
	@Autowired
	protected InitializerConfig cfg;
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), cfg.skipChecksums());
	}
	
	@Override
	public int compareTo(Loader that) {
		return this.getOrder().compareTo(that.getOrder());
	}
	
	protected abstract Domain getDomain();
	
	@Override
	public String getDomainName() {
		return getDomain().getName();
	}
	
	@Override
	public Integer getOrder() {
		return getDomain().getOrder();
	}
	
	/*
	 * This is a mere wrapper of the unsafe version when it doesn't throw anyway.
	 */
	@Override
	public void load(List<String> wildcardExclusions) {
		try {
			loadUnsafe(wildcardExclusions, false);
		}
		catch (Exception e) {}
	}
	
	@Override
	public void load() {
		load(Collections.emptyList());
	}
	
}
