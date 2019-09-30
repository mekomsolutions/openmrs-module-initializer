package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"), MDS(2, "metadatasharing"), METADATA_MAPPINGS(3, "metadatamappings"), PRIVILEGES(4,
	        "privileges"), ENCOUNTER_TYPES(5, "encountertypes"), ROLES(6, "roles"), GLOBAL_PROPERTIES(7,
	                "globalproperties"), LOCATIONS(8, "locations"), CONCEPTS(9, "concepts"), PROGRAMS(10,
	                        "programs"), PROGRAM_WORKFLOWS(11, "programworkflows"), PROGRAM_WORKFLOW_STATES(12,
	                                "programworkflowstates"), PERSON_ATTRIBUTE_TYPES(13,
	                                        "personattributetypes"), IDENTIFIER_SOURCES(14,
	                                                "idgen"), DRUGS(15, "drugs"), ORDER_FREQUENCIES(16, "orderfrequencies"), ORDER_TYPES(17, "ordertypes");
	
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
