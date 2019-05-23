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
public class ProgramWorkflowsCsvParser extends CsvParser<ProgramWorkflow, ProgramWorkflowService, BaseLineProcessor<ProgramWorkflow, ProgramWorkflowService>> {
	
	@Autowired
	public ProgramWorkflowsCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService service) {
		this.service = service;
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
		service.saveProgram(program);
		return instance;
	}
	
	@Override
	protected boolean isVoidedOrRetired(ProgramWorkflow instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new ProgramWorkflowLineProcessor(headerLine, service));
	}
}
