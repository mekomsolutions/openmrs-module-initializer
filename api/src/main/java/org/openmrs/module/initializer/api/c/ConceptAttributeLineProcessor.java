package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseAttributeLineProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("initializer.conceptAttributeLineProcessor")
public class ConceptAttributeLineProcessor extends BaseAttributeLineProcessor<Concept, ConceptAttributeType, ConceptAttribute> {
	
	private ConceptService conceptService;
	
	@Autowired
	public ConceptAttributeLineProcessor(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public ConceptAttributeType getAttributeType(String identifier) throws IllegalArgumentException {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("A blank attribute type identifier was provided.");
		}
		ConceptAttributeType ret = conceptService.getConceptAttributeTypeByUuid(identifier);
		if (ret == null) {
			ret = conceptService.getConceptAttributeTypeByName(identifier);
		}
		return ret;
	}
	
	@Override
	public ConceptAttribute newAttribute() {
		return new ConceptAttribute();
	}
	
}
