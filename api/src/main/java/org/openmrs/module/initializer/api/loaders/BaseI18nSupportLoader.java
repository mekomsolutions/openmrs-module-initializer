package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.utils.IgnoreBOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public abstract class BaseI18nSupportLoader extends BaseInputStreamLoader {
	
	@Autowired
	@Qualifier("initializer.InitializerMessageSource")
	protected InitializerMessageSource initializerMessageSource;
	
	protected abstract void loadI18nMessages(InputStream is) throws Exception;
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		
		super.loadUnsafe(wildcardExclusions, doThrow);
		
		final ConfigDirUtil dirUtil = getDirUtil();
		for (File file : dirUtil.getFiles(getFileExtension(), wildcardExclusions)) {
			try (InputStream is = new IgnoreBOMInputStream(new FileInputStream(file));) {
				loadI18nMessages(is);
				IOUtils.closeQuietly(is);
			}
			catch (Exception e) {
				
			}
		}
	}
}
