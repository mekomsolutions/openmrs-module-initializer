package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.LocationAttributeType;

public enum AttributeTypeEntity {
	LOCATION(LocationAttributeType.class.getSimpleName()), VISIT(LocationAttributeType.class.getSimpleName()), CONCEPT(
	        LocationAttributeType.class.getSimpleName()), PROVIDER(
	                LocationAttributeType.class.getSimpleName()), PROGRAM(LocationAttributeType.class.getSimpleName());
	
	private final String attTypeName;
	
	AttributeTypeEntity(String attTypeName) {
		this.attTypeName = attTypeName;
	}
	
	public String getName() {
		return attTypeName;
	}
}
