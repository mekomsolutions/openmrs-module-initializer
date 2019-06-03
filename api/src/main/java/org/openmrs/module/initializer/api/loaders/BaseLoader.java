package org.openmrs.module.initializer.api.loaders;

import org.apache.commons.logging.Log;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerLogFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all loaders.
 * 
 * @see {@link Loader}
 */
public abstract class BaseLoader implements Loader {
	
	protected final Log log = InitializerLogFactory.getLog(getClass());
	
	@Autowired
	protected InitializerService iniz;
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), iniz.getRejectionsDirPath(),
		        getDomainName());
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
	
}
