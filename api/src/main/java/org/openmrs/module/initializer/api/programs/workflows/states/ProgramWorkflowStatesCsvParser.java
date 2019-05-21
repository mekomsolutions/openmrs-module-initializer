package org.openmrs.module.initializer.api.programs.workflows.states;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowStatesCsvParser extends CsvParser<ProgramWorkflowState, BaseLineProcessor<ProgramWorkflowState>> {
	
	private ProgramWorkflowService pwfService;
	
	@Autowired
	public ProgramWorkflowStatesCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService pwfService,
	    ProgramWorkflowStateLineProcessor processor) {
		super(processor);
		this.pwfService = pwfService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAM_WORKFLOW_STATES;
	}
	
	@Override
	protected ProgramWorkflowState save(ProgramWorkflowState instance) {
		ProgramWorkflow programWorkflow = instance.getProgramWorkflow();
		if (programWorkflow != null) {
			programWorkflow.addState(instance);
		}
		Program program = programWorkflow.getProgram();
		pwfService.saveProgram(program);
		return instance;
	}
}
