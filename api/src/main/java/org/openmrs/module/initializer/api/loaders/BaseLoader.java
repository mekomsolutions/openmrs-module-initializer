package org.openmrs.module.initializer.api.loaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all loaders.
 * 
 * @see {@link Loader}
 */
public abstract class BaseLoader implements Loader {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected InitializerService iniz;
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomain());
	}
	
	@Override
	public int compareTo(Loader that) {
		return this.getOrder().compareTo(that.getOrder());
	}
	
}
