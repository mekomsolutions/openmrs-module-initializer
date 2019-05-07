package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.programs.workflows.states.ProgramWorkflowStatesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowStatesLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.PROGRAM_WORKFLOW_STATES;
	}
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new ProgramWorkflowStatesCsvParser(is, service);
	}
	
}
