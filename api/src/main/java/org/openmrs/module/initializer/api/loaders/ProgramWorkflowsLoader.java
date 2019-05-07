package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.programs.workflows.ProgramWorkflowsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowsLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_PROG_WF;
	}
	
	@Override
	public Integer getOrder() {
		return 9;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new ProgramWorkflowsCsvParser(is, service);
	}
	
}
