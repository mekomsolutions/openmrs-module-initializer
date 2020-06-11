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
	LOCATION_TAGS(9, "locationtags"),
	LOCATIONS(10, "locations"),
	BAHMNI_FORMS(11, "bahmniforms"),
	CONCEPT_CLASSES(12, "conceptclasses"),
	CONCEPTS(13, "concepts"),
	PROGRAMS(14, "programs"),
	PROGRAM_WORKFLOWS(15, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(16, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(17, "personattributetypes"),
	IDENTIFIER_SOURCES(18, "idgen"),
	AUTO_GENERATION_OPTION(19, "autogenerationoptions"),
	DRUGS(20, "drugs"),
	ORDER_FREQUENCIES(21, "orderfrequencies"),
	ORDER_TYPES(22, "ordertypes"),
	APPOINTMENTS_SPECIALITIES(23, "appointmentsspecialities"),
	APPOINTMENTS_SERVICES_DEFINITIONS(24, "appointmentsservicesdefinitions"),
	DATAFILTER_MAPPINGS(25, "datafiltermappings"),
	METADATA_SETS(26, "metadatasets"),
	METADATA_SET_MEMBERS(27, "metadatasetmembers"),
	METADATA_TERM_MAPPINGS(28, "metadatatermmappings");
	
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
