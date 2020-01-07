package org.openmrs.module.initializer.api.programs.workflows;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
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
	public ProgramWorkflow bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		String id = uuid;
		if (id == null) {
			id = line.get(ProgramWorkflowLineProcessor.HEADER_WORKFLOW_CONCEPT);
		}
		
		ProgramWorkflow wf = Utils.fetchProgramWorkflow(id, pwfService, Context.getConceptService());
		if (wf == null) {
			wf = new ProgramWorkflow();
			if (!StringUtils.isEmpty(uuid)) {
				wf.setUuid(uuid);
			}
		}
		
		return wf;
	}
	
	@Override
	public ProgramWorkflow save(ProgramWorkflow instance) {
		Program program = instance.getProgram();
		if (program != null) {
			program.addWorkflow(instance);
		}
		pwfService.saveProgram(program);
		return instance;
	}
}
