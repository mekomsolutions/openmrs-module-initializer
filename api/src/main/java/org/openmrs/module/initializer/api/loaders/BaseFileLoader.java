package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.List;

import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.OrderedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFileLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	abstract protected String getFileExtension();
	
	abstract protected void load(File file) throws Exception;
	
	/*
	 * Override this method to provide another implementation for an ordered file.
	 */
	public OrderedFile newOrderedFile(File file) {
		return new OrderedFile(file);
	}
	
	@Override
	public void load(List<String> wildcardExclusions) {
		
		final ConfigDirUtil dirUtil = getDirUtil();
		
		dirUtil.getOrderedFiles(getFileExtension(), wildcardExclusions, this).stream().forEach(file -> {
			
			try {
				load(file);
				dirUtil.writeChecksum(file, dirUtil.getChecksumIfChanged(file)); // the updated config. file is marked as processed
				log.info("The '" + getDomainName() + "' configuration file has been loaded successfuly: " + file.getPath());
				
			}
			catch (Exception e) {
				log.error("The '" + getDomainName() + "' configuration file could not be loaded: " + file.getPath(), e);
			}
			
		});
		
	}
}
