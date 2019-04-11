package org.openmrs.module.initializer.api.programs.workflows.states;

import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.api.CsvParser;

import java.io.IOException;
import java.io.InputStream;

public class ProgramWorkflowStateCsvParser extends CsvParser<ProgramWorkflowState, ProgramWorkflowService, ProgramWorkflowStateLineProcessor>{

	public ProgramWorkflowStateCsvParser(InputStream is, ProgramWorkflowService pws) throws IOException {
		super(is, pws);
	}

	@Override
	protected ProgramWorkflowState save(ProgramWorkflowState instance) {
		ProgramWorkflow programWorkflow = instance.getProgramWorkflow();
		if (programWorkflow != null) {
			programWorkflow.addState(instance);
		}
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
