package org.openmrs.module.initializer.api.programs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramLineProcessor extends BaseLineProcessor<Program> {
	
	protected static String HEADER_CONCEPT_PROGRAM = "program concept";
	
	protected static String HEADER_OUTCOMES_CONCEPT = "outcomes concept";
	
	private ConceptService conceptService;
	
	/**
	 * @param conceptService
	 */
	@Autowired
	public ProgramLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public Program fill(Program program, CsvLine line) throws IllegalArgumentException {
		
		Concept programConcept = Utils.fetchConcept(line.get(HEADER_CONCEPT_PROGRAM), conceptService);
		program.setConcept(programConcept);
		
		String name = line.getString(HEADER_NAME);
		if (StringUtils.isBlank(name)) {
			name = Utils.getBestMatchName(programConcept, Context.getLocale());
		}
		program.setName(name);
		
		String description = line.getString(HEADER_DESC);
		if (StringUtils.isBlank(description)) {
			description = Utils.getBestMatchDescription(programConcept, Context.getLocale());
		}
		program.setDescription(description);
		
		String outcomeConceptString = line.get(HEADER_OUTCOMES_CONCEPT);
		if (StringUtils.isNotBlank(outcomeConceptString)) {
			Concept outcomeConcept = Utils.fetchConcept(outcomeConceptString, conceptService, true);
			program.setOutcomesConcept(outcomeConcept);
		}
		
		return program;
	}
}
