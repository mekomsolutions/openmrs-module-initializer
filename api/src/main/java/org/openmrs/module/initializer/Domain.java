package org.openmrs.module.initializer;

import org.apache.commons.lang.ArrayUtils;

public enum Domain {
	
	JSON_KEY_VALUES,
	METADATASHARING,
	VISIT_TYPES,
	PATIENT_IDENTIFIER_TYPES,
	LOCATION_TAGS,
	PRIVILEGES,
	ENCOUNTER_TYPES,
	ROLES,
	GLOBAL_PROPERTIES,
	ATTRIBUTE_TYPES,
	LOCATIONS,
	BAHMNI_FORMS,
	CONCEPT_CLASSES,
	CONCEPTS,
	PROGRAMS,
	PROGRAM_WORKFLOWS,
	PROGRAM_WORKFLOW_STATES,
	PERSON_ATTRIBUTE_TYPES,
	IDGEN,
	AUTO_GENERATION_OPTIONS,
	DRUGS,
	ORDER_FREQUENCIES,
	ORDER_TYPES,
	APPOINTMENT_SPECIALITIES,
	APPOINTMENT_SERVICE_DEFINITIONS,
	APPOINTMENT_SERVICE_TYPES,
	DATAFILTER_MAPPINGS,
	METADATA_SETS,
	METADATA_SET_MEMBERS,
	METADATA_TERM_MAPPINGS,
	HTML_FORMS;
	
	public int getOrder() {
		return ArrayUtils.indexOf(values(), this) + 1;
	}
	
	/**
	 * The name of the domain is also the name of its subfolder inside the configuration folder.
	 */
	public String getName() {
		return name().replace("_", "").toLowerCase();
	}
}
