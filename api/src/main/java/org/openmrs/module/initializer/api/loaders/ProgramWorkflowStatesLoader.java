package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.programs.workflows.states.ProgramWorkflowStatesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowStatesLoader extends BaseCsvLoader<ProgramWorkflowStatesCsvParser> {
	
	@Autowired
	public void setParser(ProgramWorkflowStatesCsvParser parser) {
		this.parser = parser;
	}
	
}
