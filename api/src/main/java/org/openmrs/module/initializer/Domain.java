package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	PATIENT_IDENTIFIER_TYPES(3, "patientidentifiertypes"),
	LOCATION_TAGS(4, "locationtags"),
	PRIVILEGES(5, "privileges"),
	ENCOUNTER_TYPES(6, "encountertypes"),
	ROLES(7, "roles"),
	GLOBAL_PROPERTIES(8, "globalproperties"),
	ATTRIBUTE_TYPES(9, "attributetypes"),
	LOCATIONS(10, "locations"),
	LOCATION_TAG_MAPS(11, "locationtagmaps"),
	BAHMNI_FORMS(12, "bahmniforms"),
	CONCEPT_CLASSES(13, "conceptclasses"),
	CONCEPTS(14, "concepts"),
	PROGRAMS(15, "programs"),
	PROGRAM_WORKFLOWS(16, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(17, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(18, "personattributetypes"),
	IDENTIFIER_SOURCES(19, "idgen"),
	AUTO_GENERATION_OPTION(20, "autogenerationoptions"),
	DRUGS(21, "drugs"),
	ORDER_FREQUENCIES(22, "orderfrequencies"),
	ORDER_TYPES(23, "ordertypes"),
	APPOINTMENTS_SPECIALITIES(24, "appointmentsspecialities"),
	APPOINTMENTS_SERVICES_DEFINITIONS(25, "appointmentsservicesdefinitions"),
	DATAFILTER_MAPPINGS(26, "datafiltermappings"),
	METADATA_SETS(27, "metadatasets"),
	METADATA_SET_MEMBERS(28, "metadatasetmembers"),
	METADATA_TERM_MAPPINGS(29, "metadatatermmappings");
	
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
