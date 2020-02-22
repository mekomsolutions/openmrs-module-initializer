package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	PATIENT_IDENTIFIER_TYPES(3, "patientidentifiertypes"),
	METADATA_MAPPINGS(4, "metadatamappings"),
	PRIVILEGES(5, "privileges"),
	ENCOUNTER_TYPES(6, "encountertypes"),
	ROLES(7, "roles"),
	GLOBAL_PROPERTIES(8, "globalproperties"),
	ATTRIBUTE_TYPES(9, "attributetypes"),
	LOCATIONS(10, "locations"),
	CONCEPT_CLASSES(11, "conceptclasses"),
	CONCEPTS(12, "concepts"),
	PROGRAMS(13, "programs"),
	PROGRAM_WORKFLOWS(14, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(15, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(16, "personattributetypes"),
	IDENTIFIER_SOURCES(17, "idgen"),
	DRUGS(18, "drugs"),
	ORDER_FREQUENCIES(19, "orderfrequencies"),
	ORDER_TYPES(20, "ordertypes"),
	APPOINTMENTS_SPECIALITIES(21, "appointmentsspecialities"),
	APPOINTMENTS_SERVICES_DEFINITIONS(22, "appointmentsservicesdefinitions"),
	DATAFILTER_MAPPINGS(23, "datafiltermappings");
	
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
