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
	APPOINTMENT_SPECIALITIES(24, "appointmentspecialities"),
	APPOINTMENT_SERVICE_DEFINITIONS(25, "appointmentservicedefinitions"),
	APPOINTMENT_SERVICE_TYPES(26, "appointmentservicetypes"),
	DATAFILTER_MAPPINGS(27, "datafiltermappings"),
	METADATA_SETS(28, "metadatasets"),
	METADATA_SET_MEMBERS(29, "metadatasetmembers"),
	METADATA_TERM_MAPPINGS(30, "metadatatermmappings"),
	HTML_FORMS(31, "htmlforms"),
	OCL(32, "openconceptlab");
	
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
