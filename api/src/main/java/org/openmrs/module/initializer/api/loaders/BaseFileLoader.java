package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.List;

import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFileLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	abstract protected String getFileExtension();
	
	abstract protected void load(File file) throws Exception;
	
	@Override
	public void load(List<String> wildcardExclusions) {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		for (File file : dirUtil.getFiles(getFileExtension(), wildcardExclusions)) {
			
			String checksum = dirUtil.getChecksumIfChanged(file);
			if (checksum.isEmpty()) {
				continue;
			}
			
			try {
				load(file);
				dirUtil.writeChecksum(file, checksum); // the updated config. file is marked as processed
				log.info("The '" + getDomainName() + "' configuration file has been loaded successfuly: " + file.getPath());
				
			}
			catch (Exception e) {
				log.error("The '" + getDomainName() + "' configuration file could not be loaded: " + file.getPath(), e);
			}
		}
	}
}
