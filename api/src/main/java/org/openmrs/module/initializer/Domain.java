package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	VISIT_TYPES(3, "visittypes"),
	PATIENT_IDENTIFIER_TYPES(4, "patientidentifiertypes"),
	LOCATION_TAGS(5, "locationtags"),
	PRIVILEGES(6, "privileges"),
	ENCOUNTER_TYPES(7, "encountertypes"),
	ROLES(8, "roles"),
	GLOBAL_PROPERTIES(9, "globalproperties"),
	ATTRIBUTE_TYPES(10, "attributetypes"),
	LOCATIONS(11, "locations"),
	BAHMNI_FORMS(12, "bahmniforms"),
	CONCEPT_CLASSES(13, "conceptclasses"),
	CONCEPT_SOURCES(14, "conceptsources"),
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
