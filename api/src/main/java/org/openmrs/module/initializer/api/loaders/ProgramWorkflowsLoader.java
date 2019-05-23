package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.programs.workflows.ProgramWorkflowsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowsLoader extends BaseCsvLoader<ProgramWorkflowsCsvParser> {
	
	@Autowired
	public void setParser(ProgramWorkflowsCsvParser parser) {
		this.parser = parser;
	}
	
}
