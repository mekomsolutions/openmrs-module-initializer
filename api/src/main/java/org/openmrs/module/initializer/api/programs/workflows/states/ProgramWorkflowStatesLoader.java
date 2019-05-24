package org.openmrs.module.initializer.api.programs.workflows.states;

import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowStatesLoader extends BaseCsvLoader<ProgramWorkflowState, ProgramWorkflowStatesCsvParser> {
	
	@Autowired
	public void setParser(ProgramWorkflowStatesCsvParser parser) {
		this.parser = parser;
	}
}
