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
 * A loader to focus on internationalization of {@link InputStream} entries through message
 * properties loaded using {@link InitializerMessageSource}. This logic is executed after files of
 * the domain have been loaded by {@link BaseFileLoader#loadUnsafe(List, boolean)} implementation.
 * 
 * @see {@link BaseInputStreamLoader}
 * @see {@link BaseFileLoader}
 * @see {@link Loader}
 * @since 2.2.0
 */
public abstract class BaseI18nSupportLoader extends BaseInputStreamLoader {
	
	@Autowired
	@Qualifier("initializer.InitializerMessageSource")
	protected InitializerMessageSource initializerMessageSource;
	
	/**
	 * Provides for the processing (or "loading") of internationalization message properties on domain's
	 * file entries.
	 * 
	 * @param is The {@link InputStream} of woking file to process
	 */
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
