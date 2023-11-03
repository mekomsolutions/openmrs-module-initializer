package org.openmrs.module.initializer.api.loaders;

import java.nio.file.Paths;
import java.util.List;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenmrsProfile(modules = { "addresshierarchy:2.17.* - 9.*" })
public class AddressHierarchyLoader extends BaseLoader {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Domain getDomain() {
		return Domain.ADDRESS_HIERARCHY;
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		
		try {
			AddressConfigurationLoader.loadAddressConfiguration(Paths.get(iniz.getConfigDirPath()), Paths.get(iniz.getChecksumsDirPath()));
		}
		catch (Exception e) {
			log.error(e.getMessage());
			if (doThrow) {
				log.error("The loading of the '" + getDomainName() + "' configuration was aborted.", e);
				throw new RuntimeException(e);
			}
		}
	}
}
