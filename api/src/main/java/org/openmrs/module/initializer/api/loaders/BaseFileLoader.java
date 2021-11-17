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
	
	/**
	 * Turns the File instance into an OrderedFile instance.
	 * 
	 * @param file The file to a file that can be ordered.
	 * @return
	 */
	public OrderedFile toOrderedFile(File file) {
		return new OrderedFile(file);
	}
	
	/**
	 * Performs pre-loading operations with the file, such as extracting or computing data from the file
	 * that needs to be stored in memory rather than being persisted. IMPORTANT: this method will run on
	 * the file even if its checksum file says that the file should not be processed anymore. This is
	 * because the outcome of the pre-loader is transient by design.
	 * 
	 * @param file The file to be pre-loaded.
	 * @return The original file, untouched.
	 */
	protected void preload(final File file) throws Exception {
	}
	
	private File preload(final File file, boolean doThrow) {
		try {
			preload(file);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			if (doThrow) {
				log.error(
				    "The pre-loading of the '" + getDomainName() + "' configuration file was aborted:\n" + file.getPath(),
				    e);
				throw new RuntimeException(e);
			}
		}
		return file;
	}
	
	/**
	 * During pre-loading of a domain file, an exception may occur preventing its actual loading in
	 * strict mode(i.e doThrow is true). Loaders that do not have to fail loading given an exception
	 * occurred when pre-loading would have to override this function to return false.
	 * 
	 * @param doThrow whether to throw exception on not
	 * @return true or false.
	 */
	protected boolean throwingOnPreload(boolean doThrow) {
		return doThrow;
	}
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		
		final ConfigDirUtil dirUtil = getDirUtil();
		
		dirUtil.getFiles(getFileExtension(), wildcardExclusions).stream().map(f -> toOrderedFile(f)).sorted()
		        .map(f -> preload(f, throwingOnPreload(doThrow))).filter(f -> !dirUtil.getChecksumIfChanged(f).isEmpty())
		        .forEach(file -> {
			        
			        try {
				        load(file);
			        }
			        catch (Exception e) {
				        log.error(e.getMessage());
				        if (doThrow) {
					        log.error("The loading of the '" + getDomainName() + "' configuration file was aborted:\n"
					                + file.getPath(),
					            e);
					        throw new RuntimeException(e);
				        }
			        }
			        
			        dirUtil.writeChecksum(file, dirUtil.getChecksumIfChanged(file)); // the config file is marked as processed
			        log.info("The '" + getDomainName() + "' configuration file has finished loading:\n" + file.getPath());
		        });
		
	}
}
