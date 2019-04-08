package org.openmrs.module.initializer.api.programWorkflow;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

import java.io.IOException;
import java.io.InputStream;

public class ProgramWorkflowCsvParser extends CsvParser<ProgramWorkflow, ProgramWorkflowService, BaseLineProcessor<ProgramWorkflow, ProgramWorkflowService>> {
	
	public ProgramWorkflowCsvParser(InputStream is, ProgramWorkflowService pws) throws IOException {
		super(is, pws);
	}
	
	@Override
	protected ProgramWorkflow save(ProgramWorkflow instance) {
		Program program = instance.getProgram();
		if (program != null) {
			program.addWorkflow(instance);
		}
		return instance;
	}
	
	@Override
	protected boolean isVoidedOrRetired(ProgramWorkflow instance) {
		return instance.isRetired();
	}
	
	@Override
	protected ProgramWorkflow doVoidRetireActions(ProgramWorkflow instance) {
		ProgramWorkflow programWorkflow = service.getWorkflowByUuid(instance.getUuid());
		if (programWorkflow != null) {
			Program program = instance.getProgram();
			program.retireWorkflow(instance);
		}
		return programWorkflow;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new ProgramWorkflowLineProcessor(headerLine, service));
	}
}
