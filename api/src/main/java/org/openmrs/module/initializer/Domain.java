package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	CONCEPT_CLASSES(2, "conceptclasses"),
	CONCEPT_SOURCES(3, "conceptsources"),
	MDS(4, "metadatasharing"),
	VISIT_TYPES(5, "visittypes"),
	PATIENT_IDENTIFIER_TYPES(6, "patientidentifiertypes"),
	LOCATION_TAGS(7, "locationtags"),
	PRIVILEGES(8, "privileges"),
	ENCOUNTER_TYPES(9, "encountertypes"),
	ROLES(10, "roles"),
	GLOBAL_PROPERTIES(11, "globalproperties"),
	ATTRIBUTE_TYPES(12, "attributetypes"),
	LOCATIONS(13, "locations"),
	BAHMNI_FORMS(14, "bahmniforms"),
	CONCEPTS(15, "concepts"),
	PROGRAMS(16, "programs"),
	PROGRAM_WORKFLOWS(17, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(18, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(19, "personattributetypes"),
	IDENTIFIER_SOURCES(20, "idgen"),
	AUTO_GENERATION_OPTION(21, "autogenerationoptions"),
	DRUGS(22, "drugs"),
	ORDER_FREQUENCIES(23, "orderfrequencies"),
	ORDER_TYPES(24, "ordertypes"),
	APPOINTMENT_SPECIALITIES(25, "appointmentspecialities"),
	APPOINTMENT_SERVICE_DEFINITIONS(26, "appointmentservicedefinitions"),
	APPOINTMENT_SERVICE_TYPES(27, "appointmentservicetypes"),
	DATAFILTER_MAPPINGS(28, "datafiltermappings"),
	METADATA_SETS(29, "metadatasets"),
	METADATA_SET_MEMBERS(30, "metadatasetmembers"),
	METADATA_TERM_MAPPINGS(31, "metadatatermmappings"),
	HTML_FORMS(32, "htmlforms");
	
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
