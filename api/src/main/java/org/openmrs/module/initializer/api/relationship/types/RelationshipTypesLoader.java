package org.openmrs.module.initializer.api.relationship.types;

import org.openmrs.RelationshipType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelationshipTypesLoader extends BaseCsvLoader<RelationshipType, RelationshipTypesCsvParser> {
	
	@Autowired
	public void setParser(RelationshipTypesCsvParser parser) {
		this.parser = parser;
	}
}
