package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseInputStreamLoader extends BaseFileLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	abstract protected void load(InputStream is) throws Exception;
	
	@Override
	protected void load(File file) throws Exception {
		try (InputStream is = new FileInputStream(file);) {
			load(is);
			IOUtils.closeQuietly(is);
		}
	}
}
