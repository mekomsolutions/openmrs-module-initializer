package org.openmrs.module.initializer.api.programs.workflows.states;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.api.CsvParser;

import java.io.IOException;
import java.io.InputStream;

public class ProgramWorkflowStatesCsvParser extends CsvParser<ProgramWorkflowState, ProgramWorkflowService, ProgramWorkflowStateLineProcessor> {
	
	public ProgramWorkflowStatesCsvParser(InputStream is, ProgramWorkflowService pws) throws IOException {
		super(is, pws);
	}
	
	@Override
	protected ProgramWorkflowState save(ProgramWorkflowState instance) {
		ProgramWorkflow programWorkflow = instance.getProgramWorkflow();
		if (programWorkflow != null) {
			programWorkflow.addState(instance);
		}
		Program program = programWorkflow.getProgram();
		service.saveProgram(program);
		return instance;
	}
	
	@Override
	protected boolean isVoidedOrRetired(ProgramWorkflowState instance) {
		return instance.getRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new ProgramWorkflowStateLineProcessor(headerLine, service));
	}
}
