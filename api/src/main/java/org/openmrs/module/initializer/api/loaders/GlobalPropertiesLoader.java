package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;
import org.springframework.stereotype.Component;

@Component
public class GlobalPropertiesLoader extends BaseLoader {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_GP;
	}
	
	@Override
	public Integer getOrder() {
		return 4;
	}
	
	@Override
	public void load() {
		
		ConfigDirUtil dirUtil = getDirUtil();
		
		final List<GlobalProperty> globalProperties = new ArrayList<GlobalProperty>();
		for (File file : dirUtil.getFiles("xml")) { // processing all the XML files inside the domain
			
			String fileName = dirUtil.getFileName(file.getPath());
			String checksum = dirUtil.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			
			GlobalPropertiesConfig config = new GlobalPropertiesConfig();
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				config = InitializerSerializer.getGlobalPropertiesConfig(is);
				globalProperties.addAll(config.getGlobalProperties());
				dirUtil.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The global properties config. file has been processed: " + fileName);
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
