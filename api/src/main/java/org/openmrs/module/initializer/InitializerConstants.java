package org.openmrs.module.initializer;

public class InitializerConstants {
	
	/*
	 * Module ids
	 */
	public static final String MODULE_NAME = "Initializer";
	
	public static final String MODULE_ARTIFACT_ID = "initializer";
	
	public static final String MODULE_SHORT_ID = "INIT";
	
	public static final String MODULE_BASE_URL = "/" + MODULE_ARTIFACT_ID;
	
	public static final String COMPONENT_LEGACY_CONTROLLER = InitializerConstants.MODULE_ARTIFACT_ID + "." + MODULE_NAME
	        + "Controller";
	
	/*
	 * 
	 */
	public static final String DIR_NAME_CONFIG = "configuration";
	
	public static final String DIR_NAME_CHECKSUM = "configuration_checksums";
	
	/*
	 * Domains
	 * The lower-cased suffixes should be used as packages suffixes, example: 'org.openmrs.module.initializer.api.gp' 
	 */
	public static final String DOMAIN_ADDR = "addresshierarchy";
	
	public static final String DOMAIN_C = "concepts";
	
	public static final String DOMAIN_IDGEN = "idgen";
	
	public static final String DOMAIN_GP = "globalproperties";
	
	public static final String DOMAIN_MDS = "metadatasharing";
	
	public static final String DOMAIN_PAT = "personattributetypes";
	
	/*
	 * CSV line processors versions
	 */
	public static final String VERSION_C_BASE = "base"; // 'base' only
	
	public static final String VERSION_C_NESTED = "nested"; // 'base' + 'nested'
	
	public static final String VERSION_C_MAPPINGS = "mappings"; // 'base' + 'mappings'
	
	public static final String VERSION_C_NESTED_MAPPINGS = "nested_mappings"; // 'base' + 'nested' + 'mappings'
}
