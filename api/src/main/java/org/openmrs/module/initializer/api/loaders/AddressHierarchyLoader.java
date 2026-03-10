package org.openmrs.module.initializer.api.loaders;

import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.getService;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.installAddressHierarchyEntries;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.installAddressHierarchyLevels;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.installAddressTemplate;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.isMatchableLevelConfig;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.readFromFile;
import static org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader.wipeAddressHierarchy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.addresshierarchy.AddressHierarchyConstants;
import org.openmrs.module.addresshierarchy.config.AddressConfiguration;
import org.openmrs.module.addresshierarchy.config.ConfigDirUtil;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@OpenmrsProfile(modules = { "addresshierarchy:2.17.* - 9.*" })
public class AddressHierarchyLoader extends BaseLoader {
	
	protected static final Logger log = LoggerFactory.getLogger(AddressHierarchyLoader.class);
	
	protected static final String ADDR_CONFIG_FILE_NAME = "addressConfiguration.xml";
	
	@Override
	protected Domain getDomain() {
		return Domain.ADDRESS_HIERARCHY;
	}
	
	@Override
	public org.openmrs.module.initializer.api.ConfigDirUtil getDirUtil() {
		return new org.openmrs.module.initializer.api.ConfigDirUtil(iniz.getConfigDirPath(), getDomainName(), true);
	}
	
	@Override
	public void loadUnsafe(List<String> wildcardExclusions, boolean doThrow) throws Exception {
		try {
			loadAddressConfiguration(Paths.get(iniz.getConfigDirPath()), iniz.getSavedChecksums());
		}
		catch (Exception e) {
			log.error(e.getMessage());
			if (doThrow) {
				log.error("The loading of the '" + getDomainName() + "' configuration was aborted.", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	public void loadAddressConfiguration(Path configPath, Map<String, String> previousChecksums) {
		final ConfigDirUtil configUtil = new ConfigDirUtil(configPath.toString(), iniz.getChecksumsDirPath(),
		        AddressHierarchyConstants.ADDRESS_HIERARCHY_DOMAIN);
		String xmlConfigFileName = ADDR_CONFIG_FILE_NAME;
		
		File domainDir = new File(configUtil.getDomainDirPath());
		if (!domainDir.exists()) {
			log.info(
			    "Address hierarchy domain folder appears not present, skipping the loading process: " + domainDir.getPath());
			return;
		}
		File configFile = new File(domainDir, xmlConfigFileName);
		if (!configFile.exists()) {
			log.error("Address hierarchy configuration file appears invalid, skipping the loading process: "
			        + configFile.getPath());
			return;
		}
		
		//
		// Processing the XML configuraton file
		//
		AddressConfiguration addressConfiguration = readFromFile(configFile);
		boolean forceReloadEntries = false;
		
		InitializerChecksum checksum = iniz.getChecksumIfChanged(configFile.toPath());
		
		if (checksum == null) {
			log.info("Address hierarchy configuration file is unchanged, skipping it: " + xmlConfigFileName);
		} else {
			
			log.info("Address hierarchy configuration file has changed, reloading it: " + xmlConfigFileName);
			
			if (!isMatchableLevelConfig(addressConfiguration.getAddressComponents()) && !addressConfiguration.mustWipe()) {
				log.warn(
				    "The address hierarchy configuration was not loaded because of a mismatch between the exisiting and provided address hierarchy levels.");
				return;
			}
			
			if (addressConfiguration.mustWipe()) {
				log.warn("The exisiting address and address hierarchy configuration is being wiped.");
				wipeAddressHierarchy();
			}
			
			// Address template
			installAddressTemplate(addressConfiguration.getAddressTemplate());
			
			// Levels
			installAddressHierarchyLevels(addressConfiguration.getAddressComponents());
			
			iniz.saveOrUpdateChecksum(checksum);
			forceReloadEntries = true; // if anything upstream is changed, we force reload the address entries from CSV
		}
		
		//
		// Processing the CSV entries file
		//
		String csvEntriesFileName = addressConfiguration.getAddressHierarchyFile().getFilename();
		checksum = iniz.getChecksumIfChanged(new File(domainDir, csvEntriesFileName).toPath());
		
		if (checksum == null && !forceReloadEntries) {
			log.info("Address hierarchy entries CSV file is unchanged, skipping it: " + csvEntriesFileName);
		} else {
			log.info("Address hierarchy entries CSV file has changed, reloading it: " + csvEntriesFileName);
			installAddressHierarchyEntries(configUtil, addressConfiguration.getAddressHierarchyFile(),
			    forceReloadEntries || addressConfiguration.mustWipe());
			iniz.saveOrUpdateChecksum(checksum);
			
			log.info("Entries loaded, re-initializing address cache");
			getService().initializeFullAddressCache();
		}
		getService().initI18nCache();
	}
}
