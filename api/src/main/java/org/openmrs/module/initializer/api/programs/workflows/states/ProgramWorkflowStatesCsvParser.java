package org.openmrs.module.initializer.api.programs.workflows.states;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowStatesCsvParser extends CsvParser<ProgramWorkflowState, BaseLineProcessor<ProgramWorkflowState>> {
	
	private ProgramWorkflowService pwfService;
	
	private ConceptService conceptService;
	
	@Autowired
	public ProgramWorkflowStatesCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService pwfService,
	    @Qualifier("conceptService") ConceptService conceptService, ProgramWorkflowStateLineProcessor processor) {
		super(processor);
		this.pwfService = pwfService;
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAM_WORKFLOW_STATES;
	}
	
	@Override
	public ProgramWorkflowState bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		String id = uuid;
		if (id == null) {
			id = line.get(ProgramWorkflowStateLineProcessor.HEADER_STATE_CONCEPT);
		}
		
		ProgramWorkflowState state = Utils.fetchProgramWorkflowState(id, pwfService, conceptService);
		if (state == null) {
			state = new ProgramWorkflowState();
			if (!StringUtils.isEmpty(uuid)) {
				state.setUuid(uuid);
			}
			getSingleLineProcessor().fill(state, line);
		}
		
		return state;
	}
	
	@Override
	public ProgramWorkflowState save(ProgramWorkflowState instance) {
		ProgramWorkflow programWorkflow = instance.getProgramWorkflow();
		if (programWorkflow != null) {
			programWorkflow.addState(instance);
		}
		Program program = programWorkflow.getProgram();
		pwfService.saveProgram(program);
		return instance;
	}
}
