package org.openmrs.module.initializer.api.programs;

import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramsCsvParser extends CsvParser<Program, ProgramWorkflowService, ProgramLineProcessor> {
	
	@Autowired
	public ProgramsCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAMS;
	}
	
	@Override
	protected Program save(Program instance) {
		return service.saveProgram(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Program instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new ProgramLineProcessor(headerLine, service));
	}
}
