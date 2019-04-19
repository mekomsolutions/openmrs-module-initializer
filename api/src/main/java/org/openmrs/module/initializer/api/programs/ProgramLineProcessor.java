package org.openmrs.module.initializer.api.programs;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.Utils;
import org.apache.commons.lang3.StringUtils;

public class ProgramLineProcessor extends BaseLineProcessor<Program, ProgramWorkflowService> {
	
	protected static String HEADER_CONCEPT_PROGRAM = "program concept";
	
	protected static String HEADER_OUTCOMES_CONCEPT = "outcomes concept";
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 * @param service
	 */
	public ProgramLineProcessor(String[] headerLine, ProgramWorkflowService service) {
		super(headerLine, service);
	}
	
	@Override
	protected Program bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		
		String id = uuid;
		if (id == null) {
			id = line.get(HEADER_NAME);
		}
		if (id == null) {
			id = line.get(HEADER_CONCEPT_PROGRAM);
		}
		
		Program program = Utils.fetchProgram(id, service, Context.getConceptService());
		if (program == null) {
			program = new Program();
			if (!StringUtils.isEmpty(uuid)) {
				program.setUuid(uuid);
			}
		}
		
		program.setRetired(getVoidOrRetire(line.asLine()));
		
		return program;
	}
	
	@Override
	protected Program fill(Program program, CsvLine line) throws IllegalArgumentException {
		
		Concept programConcept = Utils.fetchConcept(line.get(HEADER_CONCEPT_PROGRAM), Context.getConceptService());
		program.setConcept(programConcept);
		
		String programName = Utils.getBestMatchName(programConcept, Context.getLocale());
		program.setName(programName);
		String proDescription = Utils.getBestMatchDescription(programConcept, Context.getLocale());
		program.setDescription(proDescription);
		
		Concept outcomeConcept = Utils.fetchConcept(line.get(HEADER_OUTCOMES_CONCEPT), Context.getConceptService());
		program.setOutcomesConcept(outcomeConcept);
		
		return program;
	}
}
