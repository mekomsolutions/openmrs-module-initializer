package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.List;

import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.OrderedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base loader to focus the implementation on what to do with the {@link File} for the
 * configuration file being loaded. Since virtually all domains process files, this is in practise
 * the base class to all loaders. This is the base class that provides the implementation of
 * {@link Loader#loadUnsafe(List, boolean)} with a mechanism to throw exceptions early.
 * 
 * @since 2.1.0
 */
public abstract class BaseFileLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected abstract String getFileExtension();
	
	protected abstract void load(File file) throws Exception;
	
	/*
	 * Override this method to provide another implementation for an ordered file.
	 */
	public OrderedFile newOrderedFile(File file) {
		return new OrderedFile(file);
	}
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		
		final ConfigDirUtil dirUtil = getDirUtil();
		
		for (File file : dirUtil.getOrderedFiles(getFileExtension(), wildcardExclusions, this)) {
			
			try {
				load(file);
			}
			catch (Exception e) {
				log.error(e.getMessage());
				if (doThrow) {
					log.error(
					    "The loading of the '" + getDomainName() + "' configuration file was aborted:\n" + file.getPath(),
					    e);
					throw e;
				}
			}
			finally {
				dirUtil.writeChecksum(file, dirUtil.getChecksumIfChanged(file)); // the updated config. file is marked as processed
				log.info("The '" + getDomainName() + "' configuration file has finished loading:\n" + file.getPath());
			}
			
		}
		
	}
}
