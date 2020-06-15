package org.openmrs.module.initializer.api.relationships.types;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RelationshipTypesCsvParser extends CsvParser<RelationshipType, BaseLineProcessor<RelationshipType>> {
	
	private PersonService ps;
	
	@Autowired
	public RelationshipTypesCsvParser(@Qualifier("personService") PersonService ps,
	    BaseLineProcessor<RelationshipType> lineProcessor) {
		super(lineProcessor);
		this.ps = ps;
	}
	
	@Override
	public RelationshipType bootstrap(CsvLine line) throws IllegalArgumentException {
		RelationshipType relationshipType = null;
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			relationshipType = ps.getRelationshipTypeByUuid(uuid);
		}
		if (relationshipType == null) {
			relationshipType = new RelationshipType();
			relationshipType.setUuid(uuid);
		}
		return relationshipType;
	}
	
	@Override
	public RelationshipType save(RelationshipType instance) {
		return ps.saveRelationshipType(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.RELATIONSHIP_TYPES;
	}
	
}
