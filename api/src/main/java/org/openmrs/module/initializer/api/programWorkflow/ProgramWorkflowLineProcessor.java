package org.openmrs.module.initializer.api.programWorkflow;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.impl.Utils;

public class ProgramWorkflowLineProcessor extends BaseLineProcessor<ProgramWorkflow, ProgramWorkflowService> {
	
	protected static String HEADER_PROGRAM = "program";
	
	protected static String HEADER_CONCEPT = "concept";
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 * @param service
	 */
	public ProgramWorkflowLineProcessor(String[] headerLine, ProgramWorkflowService service) {
		super(headerLine, service);
	}
	
	@Override
	protected ProgramWorkflow bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		ProgramWorkflow programWorkflow = service.getWorkflowByUuid(uuid);
		if (programWorkflow == null) {
			programWorkflow = new ProgramWorkflow();
			if (!StringUtils.isEmpty(uuid)) {
				programWorkflow.setUuid(uuid);
			}
		}
		
		programWorkflow.setRetired(getVoidOrRetire(line.asLine()));
		
		return programWorkflow;
	}
	
	@Override
	protected ProgramWorkflow fill(ProgramWorkflow programWorkflow, CsvLine line) throws IllegalArgumentException {
		
		String programWorkflowName = line.get(HEADER_NAME, true);
		programWorkflow.setName(programWorkflowName);
		
		Concept programWorkflowConcept = Utils.fetchConcept(line.get(HEADER_CONCEPT), Context.getConceptService());
		programWorkflow.setConcept(programWorkflowConcept);
		
		Program program = service.getProgramByName(line.get(HEADER_PROGRAM, true));
		programWorkflow.setProgram(program);
		
		return programWorkflow;
	}
}
