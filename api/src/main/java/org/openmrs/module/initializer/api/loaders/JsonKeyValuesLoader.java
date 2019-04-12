package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.springframework.stereotype.Component;

@Component
public class JsonKeyValuesLoader extends BaseLoader {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_JKV;
	}
	
	@Override
	public Integer getOrder() {
		return 1;
	}
	
	@Override
	public void load() {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		for (File file : dirUtil.getFiles("json")) { // processing all the JSON files inside the domain
			
			String fileName = dirUtil.getFileName(file.getPath());
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				iniz.addKeyValues(is);
				is.close();
				log.info("The following JSON key-value file was succesfully imported: " + fileName);
			}
			catch (Exception e) {
				log.error("The JSON key-value file could not be imported: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
