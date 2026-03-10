package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyConstants;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.config.AddressComponent;
import org.openmrs.module.addresshierarchy.config.AddressConfiguration;
import org.openmrs.module.addresshierarchy.config.AddressHierarchyFile;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.util.OpenmrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The logic is a copy from
 * {@link org.openmrs.module.addresshierarchy.config.AddressConfigurationLoader} except for
 * checksums loading and persisting.
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
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), getDomainName(), true);
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
		final ConfigDirUtil configUtil = new ConfigDirUtil(configPath.toString(),
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
	
	/**
	 * Wipes the existing address and address hierarchy configuration.
	 * 
	 * @note Use with care !
	 */
	public static void wipeAddressHierarchy() {
		
		getService().deleteAllAddressHierarchyEntries();
		
		while (getService().getAddressHierarchyLevelsCount() > 0) {
			getService().deleteAddressHierarchyLevel(getService().getBottomAddressHierarchyLevel());
		}
	}
	
	/**
	 * Installs the configured address template by updating the global property
	 */
	public static void installAddressTemplate(Object addressTemplate) {
		try {
			log.info("Installing address template");
			String xml = Context.getSerializationService().getDefaultSerializer().serialize(addressTemplate);
			setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to serialize and save address template", e);
		}
	}
	
	public static boolean isMatchableLevelConfig(List<AddressComponent> addressComponents) {
		if (getService().getAddressHierarchyLevelsCount() == 0) {
			return true;
		}
		for (AddressComponent component : addressComponents) {
			if (getService().getAddressHierarchyLevelByAddressField(component.getField()) == null) {
				log.warn("The address field '" + component.getField()
				        + "' provided by the configuration doesn't match any existing address level.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Install the configured address hierarchy levels Currently we only install the levels if they
	 * haven't been installed; no built-in way to edit anything other than "required" at this point
	 */
	public static void installAddressHierarchyLevels(List<AddressComponent> addressComponents) {
		
		if (getService().getAddressHierarchyLevelsCount() == 0) {
			
			log.info("Installing address hierarchy levels");
			AddressHierarchyLevel lastLevel = null;
			for (AddressComponent component : addressComponents) {
				AddressHierarchyLevel level = new AddressHierarchyLevel();
				level.setName(component.getNameMapping());
				level.setAddressField(component.getField());
				level.setRequired(component.isRequiredInHierarchy());
				level.setParent(lastLevel);
				getService().saveAddressHierarchyLevel(level);
				lastLevel = level;
			}
		} else {
			
			log.info("Updating address hierarchy levels");
			
			for (AddressComponent component : addressComponents) {
				AddressHierarchyLevel level = getService().getAddressHierarchyLevelByAddressField(component.getField());
				level.setRequired(component.isRequiredInHierarchy());
				getService().saveAddressHierarchyLevel(level);
			}
		}
	}
	
	/**
	 * Install the address hierarchy entries as defined by the AddressHierarchyFile configuration
	 */
	public static void installAddressHierarchyEntries(ConfigDirUtil configDirUtil, AddressHierarchyFile file,
	        boolean deleteEntries) {
		log.info("Installing address hierarchy entries");
		if (deleteEntries) {
			log.warn("Deleting existing address hierarchy entries");
			getService().deleteAllAddressHierarchyEntries();
		}
		
		try (InputStream is = Files
		        .newInputStream(new File(configDirUtil.getDomainDirPath(), file.getFilename()).toPath())) {
			AddressHierarchyImportUtil.importAddressHierarchyFile(is, file.getEntryDelimiter(),
			    file.getIdentifierDelimiter());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to import address hierarchy from file", e);
		}
	}
	
	/**
	 * Reads from a String representing the address configuration into an AddressConfiguration object
	 */
	public static AddressConfiguration readFromFile(File file) {
		try {
			String configuration = FileUtils.readFileToString(file, "UTF-8");
			return readFromString(configuration);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(
			        "Unable to load address configuration from configuration file.  Please check the format of this file",
			        e);
		}
	}
	
	/**
	 * Reads from a String representing the address configuration into an AddressConfiguration object
	 */
	public static AddressConfiguration readFromString(String configuration) {
		try {
			return (AddressConfiguration) getSerializer().fromXML(configuration);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(
			        "Unable to load address configuration from configuration file.  Please check the format of this file",
			        e);
		}
	}
	
	/**
	 * Writes a serialized String representing the address configuration from an AddressConfiguration
	 * object
	 */
	public static String writeToString(AddressConfiguration configuration) {
		return getSerializer().toXML(configuration);
	}
	
	/**
	 * @return the serializer instance used to load configuration from file
	 */
	public static XStream getSerializer() {
		XStream xs = new XStream(new DomDriver());
		try {
			Method allowTypeHierarchy = XStream.class.getMethod("allowTypeHierarchy", Class.class);
			allowTypeHierarchy.invoke(xs, AddressConfiguration.class);
			allowTypeHierarchy.invoke(xs, AddressComponent.class);
			allowTypeHierarchy.invoke(xs, AddressHierarchyFile.class);
			log.debug("Successfully configured address configuration serializer with allowed types");
		}
		catch (Exception e) {
			log.debug("Error configuring address configuration serializer with allowed types", e);
		}
		xs.alias("addressConfiguration", AddressConfiguration.class);
		xs.alias("addressComponent", AddressComponent.class);
		xs.alias("addressHierarchyFile", AddressHierarchyFile.class);
		return xs;
	}
	
	/**
	 * Update the global property with the given name to the given value, creating it if it doesn't
	 * exist
	 */
	public static void setGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			gp = new GlobalProperty(propertyName);
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}
	
	public static AddressHierarchyService getService() {
		return Context.getService(AddressHierarchyService.class);
	}
}
