package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	METADATA_MAPPINGS(3, "metadatamappings"),
	PRIVILEGES(4, "privileges"),
	ROLES(5, "roles"),
	GLOBAL_PROPERTIES(6, "globalproperties"),
	LOCATIONS(7, "locations"),
	CONCEPTS(8, "concepts"),
	PROGRAMS(9, "programs"),
	PROGRAM_WORKFLOWS(10, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(11, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(12, "personattributetypes"),
	IDENTIFIER_SOURCES(13, "idgen"),
	DRUGS(14, "drugs"),
	ORDER_FREQUENCIES(15, "orderfrequencies");
	
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
