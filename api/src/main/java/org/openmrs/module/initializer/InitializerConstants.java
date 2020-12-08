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
	 * Validators args
	 */
	public static final String ARG_DOMAINS = "domains";
	
	public static final String PROPS_DOMAINS = MODULE_ARTIFACT_ID + "." + ARG_DOMAINS;
	
	public static final String ARG_EXCLUDE = "exclude";
	
	public static final String PROPS_EXCLUDE = MODULE_ARTIFACT_ID + "." + ARG_EXCLUDE;
	
	/*
	 * 
	 */
	public static final String DIR_NAME_CONFIG = "configuration";
	
	public static final String DIR_NAME_CHECKSUM = "configuration_checksums";
	
	public static final String DIR_NAME_REJECTIONS = "configuration_rejections";
	
	/*
	 * Domains The lower-cased suffixes should be used as packages suffixes,
	 * example: 'org.openmrs.module.initializer.api.gp'
	 */
	public static final String DOMAIN_ADDR = "addresshierarchy";
	
	public static final String DOMAIN_MSGPROP = "messageproperties";
	
}
