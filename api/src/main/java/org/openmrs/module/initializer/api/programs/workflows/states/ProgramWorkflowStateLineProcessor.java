package org.openmrs.module.initializer.api.programs.workflows.states;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.Utils;

public class ProgramWorkflowStateLineProcessor extends BaseLineProcessor<ProgramWorkflowState, ProgramWorkflowService> {
	
	protected static String HEADER_WORKFLOW = "workflow";
	
	protected static String HEADER_STATE_CONCEPT = "state concept";
	
	protected static String HEADER_INITIAL = "initial";
	
	protected static String HEADER_TERMINAL = "terminal";
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 * @param service
	 */
	public ProgramWorkflowStateLineProcessor(String[] headerLine, ProgramWorkflowService service) {
		super(headerLine, service);
	}
	
	@Override
	protected ProgramWorkflowState bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		ProgramWorkflowState state = service.getStateByUuid(uuid);
		if (state == null) {
			state = new ProgramWorkflowState();
			if (!StringUtils.isEmpty(uuid)) {
				state.setUuid(uuid);
			}
		}
		
		state.setRetired(getVoidOrRetire(line.asLine()));
		
		return state;
	}
	
	@Override
	protected ProgramWorkflowState fill(ProgramWorkflowState state, CsvLine line) throws IllegalArgumentException {
		
		Concept c = Utils.fetchConcept(line.get(HEADER_STATE_CONCEPT), Context.getConceptService());
		state.setConcept(c);
		
		String name = Utils.getBestMatchName(c, Context.getLocale());
		state.setName(name);
		String desc = Utils.getBestMatchDescription(c, Context.getLocale());
		state.setDescription(desc);
		
		state.setInitial(BooleanUtils.toBoolean(line.get(HEADER_INITIAL, true)));
		state.setTerminal(BooleanUtils.toBoolean(line.get(HEADER_TERMINAL, true)));
		
		ProgramWorkflow wf = Utils.fetchProgramWorkflow(line.get(HEADER_WORKFLOW, true), service,
		    Context.getConceptService());
		
		// states must be bound to a workflow
		if (wf == null) {
			throw new IllegalArgumentException("No workflow could be fetched from the CSV line: '" + line.toString() + "'.");
		}
		// states linked to a workflow can't be moved to another workflow
		if (state.getProgramWorkflow() != null && !wf.equals(state.getProgramWorkflow())) {
			throw new IllegalArgumentException("A State ('" + state.getName() + "') already linked to a workflow ('"
			        + state.getProgramWorkflow().getName() + "') cannot be added to another workflow, CSV line: '"
			        + line.toString() + "'.");
		}
		state.setProgramWorkflow(wf);
		
		return state;
	}
}
