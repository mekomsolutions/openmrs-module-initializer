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
import org.openmrs.module.initializer.api.impl.Utils;

public class ProgramWorkflowStateLineProcessor extends BaseLineProcessor<ProgramWorkflowState,ProgramWorkflowService> {

	protected static String HEADER_PROGRAM_WORKFLOW = "programWorkflow";

	protected static String HEADER_WORKFLOW_STATE_CONCEPT = "state concept";

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
		ProgramWorkflowState programWorkflowState = service.getStateByUuid(uuid);
		if (programWorkflowState == null) {
			programWorkflowState = new ProgramWorkflowState();
			if (!StringUtils.isEmpty(uuid)) {
				programWorkflowState.setUuid(uuid);
			}
		}

		programWorkflowState.setRetired(getVoidOrRetire(line.asLine()));

		return programWorkflowState;
	}

	@Override
	protected ProgramWorkflowState fill(ProgramWorkflowState programWorkflowState, CsvLine line) throws IllegalArgumentException {

		Concept programWorkflowStateConcept = Utils.fetchConcept(line.get(HEADER_WORKFLOW_STATE_CONCEPT), Context.getConceptService());
		programWorkflowState.setConcept(programWorkflowStateConcept);

		String programWorkflowStateName = Utils.getBestMatchName(programWorkflowStateConcept, Context.getLocale());
		programWorkflowState.setName(programWorkflowStateName);
		String programWorkflowStateDescription = Utils.getBestMatchDescription(programWorkflowStateConcept, Context.getLocale());
		programWorkflowState.setDescription(programWorkflowStateDescription);

		String initial = line.get(HEADER_INITIAL,true);
		programWorkflowState.setInitial(BooleanUtils.toBoolean(initial));

		String terminal = line.get(HEADER_TERMINAL, true);
		programWorkflowState.setTerminal(BooleanUtils.toBoolean(terminal));

		ProgramWorkflow programWorkflow = service.getWorkflowByUuid(line.get(HEADER_PROGRAM_WORKFLOW, true));
		programWorkflowState.setProgramWorkflow(programWorkflow);

		return programWorkflowState;
	}
}
