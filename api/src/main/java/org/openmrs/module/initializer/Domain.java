package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"), MDS(2, "metadatasharing"), METADATA_MAPPINGS(3, "metadatamappings"), PRIVILEGES(4,
	        "privileges"), ENCOUNTER_TYPES(5, "encountertypes"), ROLES(6, "roles"), GLOBAL_PROPERTIES(7,
	                "globalproperties"), ATTRIBUTE_TYPES(8, "attributetypes"), LOCATIONS(9, "locations"), CONCEPT_CLASSES(10,
	                        "conceptclasses"), CONCEPTS(11, "concepts"), PROGRAMS(12,
	                                "programs"), PROGRAM_WORKFLOWS(13, "programworkflows"), PROGRAM_WORKFLOW_STATES(14,
	                                        "programworkflowstates"), PERSON_ATTRIBUTE_TYPES(15,
	                                                "personattributetypes"), IDENTIFIER_SOURCES(16, "idgen"), DRUGS(17,
	                                                        "drugs"), ORDER_FREQUENCIES(18, "orderfrequencies"), ORDER_TYPES(
	                                                                19, "ordertypes"), APPOINTMENTS_SPECIALITIES(20,
	                                                                        "appointmentsspecialities"), APPOINTMENTS_SERVICES_DEFINITIONS(
	                                                                                21,
	                                                                                "appointmentsservicesdefinitions"), DATAFILTER_MAPPINGS(
	                                                                                        22, "datafiltermappings");
	
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
