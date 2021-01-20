package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;

/**
 * A base loader to focus the implementation on what to do with the {@link InputStream} for the
 * configuration file being loaded.
 */
public abstract class BaseInputStreamLoader extends BaseFileLoader {
	
	private File loadedFile = null;
	
	private void setLoadedFile(File file) {
		loadedFile = file;
	}
	
	/**
	 * Provides a reference to the file being currently loaded.
	 * 
	 * @return The file being currently loaded.
	 */
	protected File getLoadedFile() {
		return loadedFile;
	}
	
	abstract protected void load(InputStream is) throws Exception;
	
	@Override
	protected void load(File file) throws Exception {
		setLoadedFile(file);
		try (InputStream is = new IgnoreBOMInputStream(new FileInputStream(file));) {
			load(is);
			IOUtils.closeQuietly(is);
		}
		finally {
			setLoadedFile(null);
		}
	}
}
