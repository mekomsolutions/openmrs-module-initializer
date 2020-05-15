package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	PATIENT_IDENTIFIER_TYPES(3, "patientidentifiertypes"),
	PRIVILEGES(4, "privileges"),
	ENCOUNTER_TYPES(5, "encountertypes"),
	ROLES(6, "roles"),
	GLOBAL_PROPERTIES(7, "globalproperties"),
	ATTRIBUTE_TYPES(8, "attributetypes"),
	LOCATIONS(9, "locations"),
	BAHMNI_FORMS(10, "bahmniforms"),
	CONCEPT_CLASSES(11, "conceptclasses"),
	CONCEPTS(12, "concepts"),
	PROGRAMS(13, "programs"),
	PROGRAM_WORKFLOWS(14, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(15, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(16, "personattributetypes"),
	IDENTIFIER_SOURCES(17, "idgen"),
	AUTO_GENERATION_OPTION(18, "autogenerationoptions"),
	DRUGS(19, "drugs"),
	ORDER_FREQUENCIES(20, "orderfrequencies"),
	ORDER_TYPES(21, "ordertypes"),
	APPOINTMENTS_SPECIALITIES(22, "appointmentsspecialities"),
	APPOINTMENTS_SERVICES_DEFINITIONS(23, "appointmentsservicesdefinitions"),
	DATAFILTER_MAPPINGS(24, "datafiltermappings"),
	METADATA_SETS(25, "metadatasets"),
	METADATA_SET_MEMBERS(26, "metadatasetmembers"),
	METADATA_TERM_MAPPINGS(27, "metadatatermmappings");
	
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
