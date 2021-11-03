package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component("initializer.conceptSetLineProcessor")
public class ConceptSetLineProcessor extends BaseLineProcessor<Concept> {
	
	final public static String HEADER_CONCEPT = "concept";
	
	final public static String HEADER_MEMBER = "member";
	
	final public static String HEADER_MEMBER_TYPE = "member type";
	
	final public static String HEADER_SORT_WEIGHT = "sort weight";
	
	final public static String HEADER_MEMBER_TYPE_Q_AND_A = "q-and-a";
	
	final public static String HEADER_MEMBER_TYPE_CONCEPT_SET = "concept-set";
	
	protected ConceptService conceptService;
	
	@Autowired
	public ConceptSetLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	/*
	 * This is the base concept implementation.
	 */
	@Override
	public Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		// First, get the member that we are trying to add
		String memberLookup = line.get(ConceptSetLineProcessor.HEADER_MEMBER, true);
		Concept member = Utils.fetchConcept(memberLookup, conceptService);
		if (member == null) {
			throw new IllegalArgumentException("No concept found with identifier: " + memberLookup);
		}
		
		// Next, get member type or infer it from the Concept
		String memberType = line.get(HEADER_MEMBER_TYPE);
		if (StringUtils.isEmpty(memberType)) {
			if (BooleanUtils.isTrue(concept.getSet())) {
				memberType = HEADER_MEMBER_TYPE_CONCEPT_SET;
			} else {
				memberType = HEADER_MEMBER_TYPE_Q_AND_A;
			}
		}
		
		Boolean retire = getVoidOrRetire(line);
		Double sortWeight = line.getDouble(HEADER_SORT_WEIGHT);
		
		if (HEADER_MEMBER_TYPE_CONCEPT_SET.equalsIgnoreCase(memberType)) {
			boolean updated = false;
			for (Iterator<ConceptSet> iterator = concept.getConceptSets().iterator(); iterator.hasNext();) {
				ConceptSet conceptSet = iterator.next();
				if (conceptSet.getConcept().equals(member)) {
					updated = true;
					conceptSet.setSortWeight(sortWeight);
					if (retire) {
						iterator.remove();
					}
				}
			}
			if (!updated && !retire) {
				if (concept.getConceptSets() == null) {
					concept.setConceptSets(new HashSet<>());
				}
				concept.getConceptSets().add(new ConceptSet(member, sortWeight));
			}
		} else if (HEADER_MEMBER_TYPE_Q_AND_A.equalsIgnoreCase(memberType)) {
			boolean updated = false;
			for (Iterator<ConceptAnswer> iterator = concept.getAnswers().iterator(); iterator.hasNext();) {
				ConceptAnswer conceptAnswer = iterator.next();
				if (conceptAnswer.getConcept().equals(member)) {
					updated = true;
					if (retire) {
						iterator.remove();
					}
				}
			}
			if (!updated && !retire) {
				if (concept.getAnswers() == null) {
					concept.setAnswers(new HashSet<>());
				}
				ConceptAnswer conceptAnswer = new ConceptAnswer(member);
				conceptAnswer.setSortWeight(sortWeight);
				concept.addAnswer(conceptAnswer);
			}
		} else {
			throw new IllegalArgumentException("Unable to determinew whether to add as an answer or a set: " + line);
		}
		
		return concept;
	}
}
