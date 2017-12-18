package org.openmrs.module.initializer.api.c;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.impl.Utils;
import org.springframework.util.CollectionUtils;

public class NestedConceptLineProcessor extends BaseConceptLineProcessor {
	
	protected static String HEADER_ANSWERS = "answers";
	
	protected static String HEADER_MEMBERS = "members";
	
	public NestedConceptLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	/**
	 * Parses a list of concepts provided as concept mappings, UUIDs or concept names into a list of
	 * {@link Concept} instances.
	 * 
	 * @param conceptList Eg.: ["cambodia:123"; "a92bf372-2fca-11e7-93ae-92361f002671";
	 *            "CONCEPT_FULLY_SPECIFIED_NAME"]
	 * @param cs
	 * @return The list of {@link Concept} that have been found, null if any error(s).
	 */
	protected static List<Concept> parseConceptList(String conceptList, ConceptService cs) throws IllegalArgumentException {
		List<Concept> concepts = new ArrayList<Concept>();
		
		String[] parts = conceptList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String id : parts) {
			id = id.trim();
			Concept child = Utils.fetchConcept(id, cs);
			if (child != null) {
				concepts.add(child);
			} else {
				throw new IllegalArgumentException("The concept identified by '" + id
				        + "' could not be found in database. The concept with the following nested list of concepts was not created/updated: ["
				        + conceptList + "].");
			}
		}
		
		return concepts;
	}
	
	protected Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		if (!CollectionUtils.isEmpty(concept.getAnswers())) {
			concept.getAnswers().clear();
		}
		String childrenStr;
		childrenStr = line.get(HEADER_ANSWERS);
		if (!StringUtils.isEmpty(childrenStr)) {
			for (Concept child : parseConceptList(childrenStr, service)) {
				concept.addAnswer(new ConceptAnswer(child));
			}
		}
		
		if (!CollectionUtils.isEmpty(concept.getConceptSets())) {
			concept.getConceptSets().clear();
			concept.setSet(false);
		}
		childrenStr = line.get(HEADER_MEMBERS);
		if (!StringUtils.isEmpty(childrenStr)) {
			for (Concept child : parseConceptList(childrenStr, service)) {
				concept.addSetMember(child);
			}
			concept.setSet(true);
		}
		
		return concept;
	}
}
