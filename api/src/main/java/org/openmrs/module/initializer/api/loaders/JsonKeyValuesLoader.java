package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerLogFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.springframework.stereotype.Component;

@Component
public class JsonKeyValuesLoader extends BaseLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.JSON_KEY_VALUES;
	}
	
	private final Log log = InitializerLogFactory.getLog(getClass());
	
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
