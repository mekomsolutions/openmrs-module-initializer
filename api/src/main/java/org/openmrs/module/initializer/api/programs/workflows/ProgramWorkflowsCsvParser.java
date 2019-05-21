package org.openmrs.module.initializer.api.programs.workflows;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowsCsvParser extends CsvParser<ProgramWorkflow, BaseLineProcessor<ProgramWorkflow>> {
	
	private ProgramWorkflowService pwfService;
	
	@Autowired
	public ProgramWorkflowsCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService pwfService,
	    ProgramWorkflowLineProcessor processor) {
		super(processor);
		this.pwfService = pwfService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAM_WORKFLOWS;
	}
	
	@Override
	protected ProgramWorkflow save(ProgramWorkflow instance) {
		Program program = instance.getProgram();
		if (program != null) {
			program.addWorkflow(instance);
		}
		pwfService.saveProgram(program);
		return instance;
	}
}
