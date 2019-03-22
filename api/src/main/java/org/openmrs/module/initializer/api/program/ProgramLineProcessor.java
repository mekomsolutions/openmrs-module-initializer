package org.openmrs.module.initializer.api.program;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.initializer.api.impl.Utils;

import javax.rmi.CORBA.Util;

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
		Program program = service.getProgramByUuid(uuid);
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
