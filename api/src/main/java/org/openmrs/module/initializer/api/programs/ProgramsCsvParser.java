package org.openmrs.module.initializer.api.programs;

import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramsCsvParser extends CsvParser<Program, BaseLineProcessor<Program>> {
	
	private ProgramWorkflowService pwfService;
	
	@Autowired
	public ProgramsCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService pwfService,
	    ProgramLineProcessor processor) {
		super(processor);
		this.pwfService = pwfService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAMS;
	}
	
	@Override
	protected Program save(Program instance) {
		return pwfService.saveProgram(instance);
	}
}
