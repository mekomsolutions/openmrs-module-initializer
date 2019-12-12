package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"), MDS(2, "metadatasharing"), METADATA_MAPPINGS(3, "metadatamappings"), PRIVILEGES(4,
	        "privileges"), ENCOUNTER_TYPES(5, "encountertypes"), ROLES(6, "roles"), GLOBAL_PROPERTIES(7,
	                "globalproperties"), ATTRIBUTE_TYPES(8, "attributetypes"), LOCATIONS(9,
	                        "locations"), CONCEPTS(10, "concepts"), PROGRAMS(11,
	                                "programs"), PROGRAM_WORKFLOWS(12, "programworkflows"), PROGRAM_WORKFLOW_STATES(13,
	                                        "programworkflowstates"), PERSON_ATTRIBUTE_TYPES(14,
	                                                "personattributetypes"), IDENTIFIER_SOURCES(15, "idgen"), DRUGS(16,
	                                                        "drugs"), ORDER_FREQUENCIES(17, "orderfrequencies"), ORDER_TYPES(
	                                                                18, "ordertypes"), APPOINTMENTS_SPECIALITIES(19,
	                                                                        "appointmentsspecialities"), DATAFILTER_MAPPINGS(
	                                                                                20, "datafiltermappings");
	
	private final int order;
	
	private final String name;
	
	Domain(final int order, final String name) {
		this.order = order;
		this.name = name;
	}
	
	public int getOrder() {
		return order;
	}
	
	/**
	 * The name of the domain is also the name of its subfolder inside the configuration folder.
	 */
	public String getName() {
		return name;
	}
}
