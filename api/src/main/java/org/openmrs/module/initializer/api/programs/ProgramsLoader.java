package org.openmrs.module.initializer.api.programs;

import org.openmrs.Program;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramsLoader extends BaseCsvLoader<Program, ProgramsCsvParser> {
	
	@Autowired
	public void setParser(ProgramsCsvParser parser) {
		this.parser = parser;
	}
	
}
