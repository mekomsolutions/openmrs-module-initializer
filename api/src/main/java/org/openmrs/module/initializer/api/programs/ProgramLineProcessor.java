package org.openmrs.module.initializer.api.programs;

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
	 * @param headerLine The header line the processor will refer to.
	 * @param service
	 */
	@Autowired
	public ProgramLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public Program fill(Program program, CsvLine line) throws IllegalArgumentException {
		
		Concept programConcept = Utils.fetchConcept(line.get(HEADER_CONCEPT_PROGRAM), conceptService);
		program.setConcept(programConcept);
		
		String programName = Utils.getBestMatchName(programConcept, Context.getLocale());
		program.setName(programName);
		String proDescription = Utils.getBestMatchDescription(programConcept, Context.getLocale());
		program.setDescription(proDescription);
		
		Concept outcomeConcept = Utils.fetchConcept(line.get(HEADER_OUTCOMES_CONCEPT), conceptService);
		program.setOutcomesConcept(outcomeConcept);
		
		return program;
	}
}
