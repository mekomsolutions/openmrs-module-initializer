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
	
	public static final String PROPS_SKIPCHECKSUMS = MODULE_ARTIFACT_ID + "." + "skip.checksums";
	
	/*
	 * Startup properties
	 */
	public static final String PROPS_STARTUP_LOAD = MODULE_ARTIFACT_ID + "." + "startup.load";
	
	public static final String PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR = "continue_on_error";
	
	public static final String PROPS_STARTUP_LOAD_FAIL_ON_ERROR = "fail_on_error";
	
	public static final String PROPS_STARTUP_LOAD_DISABLED = "disabled";
	
	public static final String GP_INITIALIZER_SIGNATURE = "initializer.lastSignature";
	
	public static final String PROPS_PRIMARY_STARTUP = "primary.startup";
	
	/*
	 * Logging properties
	 */
	private static final String PROPS_LOGGING_PREFIX = MODULE_ARTIFACT_ID + ".logging.";
	
	public static final String PROPS_LOGGING_ENABLED = PROPS_LOGGING_PREFIX + "enabled";
	
	public static final String PROPS_LOGGING_LOCATION = PROPS_LOGGING_PREFIX + "location";
	
	public static final String PROPS_LOGGING_LEVEL = PROPS_LOGGING_PREFIX + "level";
	
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
	
	/* 
	 * Default void/retire reasons
	 */
	public static final String DEFAULT_RETIRE_REASON = "Retired by module " + InitializerConstants.MODULE_NAME;
	
	public static final String DEFAULT_VOID_REASON = "Voided by module " + InitializerConstants.MODULE_NAME;
	
	/*
	 * UUID Namespacing
	 *
	 * Used to ensure that type 3 UUIDs are properly namespaced for a domain
	 */
	public static final String CONCEPT_NAME_NAMESPACE_UUID = "103e0f29-b7a3-4382-bfdc-449068e9d436";
}
