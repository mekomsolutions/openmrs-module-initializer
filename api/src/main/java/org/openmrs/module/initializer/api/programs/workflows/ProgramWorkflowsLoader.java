package org.openmrs.module.initializer.api.programs.workflows;

import org.openmrs.ProgramWorkflow;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowsLoader extends BaseCsvLoader<ProgramWorkflow, ProgramWorkflowsCsvParser> {
	
	@Autowired
	public void setParser(ProgramWorkflowsCsvParser parser) {
		this.parser = parser;
	}
	
}
