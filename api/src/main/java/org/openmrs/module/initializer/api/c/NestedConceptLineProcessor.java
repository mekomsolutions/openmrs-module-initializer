package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component("initializer.nestedConceptLineProcessor")
public class NestedConceptLineProcessor extends ConceptLineProcessor {
	
	protected static String HEADER_ANSWERS = "answers";
	
	protected static String HEADER_MEMBERS = "members";
	
	protected ConceptListParser listParser;
	
	@Autowired
	public NestedConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService,
	    ConceptListParser listParser) {
		super(conceptService);
		this.listParser = listParser;
	}
	
	protected Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		if (!CollectionUtils.isEmpty(concept.getAnswers())) {
			concept.getAnswers().clear();
		}
		String childrenStr;
		childrenStr = line.get(HEADER_ANSWERS);
		if (!StringUtils.isEmpty(childrenStr)) {
			for (Concept child : listParser.parseList(childrenStr)) {
				concept.addAnswer(new ConceptAnswer(child));
			}
		}
		
		if (!CollectionUtils.isEmpty(concept.getConceptSets())) {
			concept.getConceptSets().clear();
			concept.setSet(false);
		}
		childrenStr = line.get(HEADER_MEMBERS);
		if (!StringUtils.isEmpty(childrenStr)) {
			for (Concept child : listParser.parseList(childrenStr)) {
				concept.addSetMember(child);
			}
			concept.setSet(true);
		}
		
		return concept;
	}
}
