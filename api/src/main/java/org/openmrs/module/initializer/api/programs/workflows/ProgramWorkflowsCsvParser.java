package org.openmrs.module.initializer.api.programs.workflows;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

public class ProgramWorkflowsCsvParser extends CsvParser<ProgramWorkflow, ProgramWorkflowService, BaseLineProcessor<ProgramWorkflow, ProgramWorkflowService>> {
	
	public ProgramWorkflowsCsvParser(InputStream is, ProgramWorkflowService pws) throws IOException {
		super(is, pws);
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
		addLineProcessor(new ProgramWorkflowLineProcessor(headerLine, service));
	}
}
