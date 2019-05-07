package org.openmrs.module.initializer;

public enum Domain {
	
	JSON_KEY_VALUES(1, "jsonkeyvalues"),
	MDS(2, "metadatasharing"),
	PRIVILEGES(3, "privileges"),
	ROLES(4, "roles"),
	GLOBAL_PROPERTIES(5, "globalproperties"),
	LOCATIONS(6, "locations"),
	CONCEPTS(7, "concepts"),
	PROGRAMS(8, "programs"),
	PROGRAM_WORKFLOWS(9, "programworkflows"),
	PROGRAM_WORKFLOW_STATES(10, "programworkflowstates"),
	PERSON_ATTRIBUTE_TYPES(11, "personattributetypes"),
	IDENTIFIER_SOURCES(12, "idgen"),
	DRUGS(13, "drugs"),
	ORDER_FREQUENCIES(14, "orderfrequencies");
	
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
