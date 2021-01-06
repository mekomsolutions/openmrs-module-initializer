package org.openmrs.module.initializer.api.gp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.loaders.BaseLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalPropertiesLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Domain getDomain() {
		return Domain.GLOBAL_PROPERTIES;
	}
	
	@Override
	public void load(List<String> wildcardExclusions) {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		final List<GlobalProperty> globalProperties = new ArrayList<GlobalProperty>();
		for (File file : dirUtil.getFiles("xml", wildcardExclusions)) { // processing all the XML files inside the domain
			
			String checksum = dirUtil.getChecksumIfChanged(file);
			if (checksum.isEmpty()) {
				continue;
			}
			
			GlobalPropertiesConfig config = new GlobalPropertiesConfig();
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				config = InitializerSerializer.getGlobalPropertiesConfig(is);
				globalProperties.addAll(config.getGlobalProperties());
				dirUtil.writeChecksum(file, checksum); // the updated config. file is marked as processed
				log.info("The global properties config. file has been processed: " + file.getPath());
			}
			catch (Exception e) {
				log.error("Could not load the global properties from: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
		
		log.info("Saving the global properties.");
		Context.getAdministrationService().saveGlobalProperties(globalProperties);
	}
}
