package org.openmrs.module.initializer.api.utils;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

public class ConceptListParser extends ListParser<Concept> {
	
	private ConceptService cs;
	
	public ConceptListParser(ConceptService cs) {
		this.cs = cs;
	}
	
	@Override
	protected Concept fetch(String id) {
		return Utils.fetchConcept(id, cs);
	}
	
}
